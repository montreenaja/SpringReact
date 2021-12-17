package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Project;
import com.example.demo.exceptions.ProjectIdException;
import com.example.demo.repositories.ProjectRepository;

@Service
public class ProjectService {
	@Autowired
	private ProjectRepository projectRepository;
	public Project saveOrUpdateProject(Project project) {
		try {
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			return projectRepository.save(project);
		}catch(Exception ex) {
			throw new ProjectIdException("Project ID '"+project.getProjectIdentifier().toUpperCase()+"' already exists"); 
			
		}		
	}
	
	public Project findProjectByIdentifier(String projectId) {
		Project project  = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		if(project == null) {
			throw new ProjectIdException("Project ID '"+projectId+"' does not exist");			
		}
		return project;
	}
	public Iterable<Project> findAllProjects(){
		return projectRepository.findAll();	
	}
	public void deleteProjectByIdentifier(String projectId) {
		Project project =  projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if(project == null) {
			throw new ProjectIdException("Cannot Project with ID '"+projectId+"'. This project does not exist");		
		}
		projectRepository.delete(project);
	}
//	public Project updateProject(Project project) {
//		try {
//			project.setId(project.getId());
//			return projectRepository.save(project);
//		}catch(Exception ex) {
//			throw new ProjectIdException("ID '"+project.getId()+"' update success"); 
//			
//		}	
//	}
}
