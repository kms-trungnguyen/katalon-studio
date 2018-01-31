package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.groovy.util.GroovyUtil;

public class ProjectFileServiceManager {

    private static final String MIGRATE_LEGACY_GLOBALVARIABLE_VS = "5.4.0";

    public static ProjectEntity addNewProject(String name, String description, short pageLoadTimeout,
            String projectLocation) throws Exception {

        // remove the "\\" post-fix
        if (projectLocation.endsWith(File.separator)) {
            projectLocation = projectLocation.substring(0, projectLocation.length() - 1);
        }
        File projectFolder = new File(projectLocation + File.separator + name);
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }

        ProjectEntity project = new ProjectEntity();
        project.setUUID(Util.generateGuid());
        project.setFolderLocation(projectFolder.getAbsolutePath());
        project.setName(name);
        project.setDescription(description);
        project.setPageLoadTimeout(pageLoadTimeout);
        project.setMigratedVersion(MIGRATE_LEGACY_GLOBALVARIABLE_VS);

        EntityService.getInstance().saveEntity(project);
        FolderFileServiceManager.initRootEntityFolders(project);
        createSettingFolder(project);

        GlobalVariableFileServiceManager.newProfile(ExecutionProfileEntity.DF_PROFILE_NAME, true,
                Collections.emptyList(), project);

        GroovyUtil.initGroovyProject(project,
                FolderFileServiceManager.loadAllTestCaseDescendants(FolderFileServiceManager.getTestCaseRoot(project)),
                null);

        return project;
    }

    public static ProjectEntity getProject(String projectFileLocation) throws Exception {
        File projectFile = new File(projectFileLocation);
        if (projectFile.isFile() && projectFile.exists()) {
            ProjectEntity project = (ProjectEntity) EntityService.getInstance().getEntityByPath(projectFileLocation);
            project.setFolderLocation(projectFile.getParent());
            createSettingFolder(project);
            return project;
        }
        return null;
    }

    public static ProjectEntity openProject(String projectFileLocation) throws Exception {
        ProjectEntity project = openProjectWithoutClasspath(projectFileLocation);
        if (project != null) {
            GroovyUtil.openGroovyProject(project, FolderFileServiceManager
                    .loadAllTestCaseDescendants(FolderFileServiceManager.getTestCaseRoot(project)));
        }
        return project;
    }

    public static ProjectEntity openProjectWithoutClasspath(String projectFileLocation) throws Exception {
        File projectFile = new File(projectFileLocation);
        if (projectFile.isFile() && projectFile.exists()) {
            ProjectEntity project = (ProjectEntity) EntityService.getInstance().getEntityByPath(projectFileLocation);
            project.setFolderLocation(projectFile.getParent());
            createSettingFolder(project);
            FolderFileServiceManager.initRootEntityFolders(project);

            if (!MIGRATE_LEGACY_GLOBALVARIABLE_VS.equals(project.getMigratedVersion())) {
                migrateLegacyGlobalVariable(project);
                project.setMigratedVersion(MIGRATE_LEGACY_GLOBALVARIABLE_VS);

                EntityService.getInstance().saveEntity(project);
            }
            if (GlobalVariableFileServiceManager.getAll(project).isEmpty()) {
                GlobalVariableFileServiceManager.newProfile(ExecutionProfileEntity.DF_PROFILE_NAME, true,
                        Collections.emptyList(), project);
            }
            return project;
        }
        return null;
    }

    private static void migrateLegacyGlobalVariable(ProjectEntity project) throws Exception {
        ExecutionProfileEntity legacyGlobalVariable = (ExecutionProfileEntity) EntityService.getInstance()
                .getEntityByPath(FileServiceConstant.getLegacyGlobalVariableFileLocation(project.getFolderLocation()));

        GlobalVariableFileServiceManager.newProfile(ExecutionProfileEntity.DF_PROFILE_NAME, true,
                legacyGlobalVariable.getGlobalVariableEntities(), project);
    }

    public static ProjectEntity updateProject(String name, String description, String projectFileLocation,
            short pageLoadTimeout) throws Exception {
        ProjectEntity project = getProject(projectFileLocation);

        IProject oldGroovyProject = GroovyUtil.getGroovyProject(project);

        project.setName(name);
        project.setDescription(description);
        project.setPageLoadTimeout(pageLoadTimeout);

        // name changed
        if (!project.getLocation().equals(projectFileLocation)) {
            EntityService.getInstance().getEntityCache().remove(project, true);
            try {
                GroovyUtil.updateGroovyProject(project, oldGroovyProject);
            } catch (CoreException ex) {
                throw new DALException(StringConstants.MNG_EXC_FAILED_TO_UPDATE_PROJ);
            }
        }

        EntityService.getInstance().saveEntity(project);

        return project;
    }

    public static boolean isDuplicationProjectName(String name, String projectFolderLocation) throws Exception {
        EntityService.getInstance().validateName(name);
        return (getProject(projectFolderLocation + File.separator + name + File.separator + name
                + ProjectEntity.getProjectFileExtension()) != null);
    }

    private static void createSettingFolder(ProjectEntity project) throws IOException {
        File settingFolder = new File(project.getFolderLocation() + File.separator + FileServiceConstant.SETTING_DIR);
        if (!settingFolder.exists()) {
            settingFolder.mkdir();
        }

        File externalSettingFolder = new File(
                project.getFolderLocation() + File.separator + FileServiceConstant.EXTERNAL_SETTING_DIR);
        if (!externalSettingFolder.exists()) {
            externalSettingFolder.mkdir();
        }

        File internalSettingFolder = new File(
                project.getFolderLocation() + File.separator + FileServiceConstant.INTERNAL_SETTING_DIR);
        if (!internalSettingFolder.exists()) {
            internalSettingFolder.mkdir();
        }
    }
}
