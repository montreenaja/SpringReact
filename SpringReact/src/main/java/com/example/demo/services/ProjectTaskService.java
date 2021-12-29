package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Backlog;
import com.example.demo.domain.Project;
import com.example.demo.domain.ProjectTask;
import com.example.demo.exceptions.ProjectNotFoundException;
import com.example.demo.repositories.BacklogRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private BacklogRepository backlogRepository;

	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;

	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {

		try {

			Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
			projectTask.setBacklog(backlog);
			Integer BacklogSequence = backlog.getPTSequence();
			BacklogSequence++;

			backlog.setPTSequence(BacklogSequence);
			projectTask.setProjectSequence(backlog.getProjectIdentifier() + "-" + BacklogSequence);
			projectTask.setProjectIdentifier(projectIdentifier);

			// projectTask.getPriority() == 0 handle in form
			if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
				projectTask.setPriority(3);
			}
			if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
				projectTask.setStatus("TO_DO");
			}
			return projectTaskRepository.save(projectTask);
		} catch (Exception ex) {
			throw new ProjectNotFoundException("Project Not Found");
		}
	}

	public Iterable<ProjectTask> findBacklogById(String id) {
		Project project = projectRepository.findByProjectIdentifier(id);
		
		if(project == null) {
			throw new ProjectNotFoundException("Project with ID: '"+id+"' does not exist");		
		}
		
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
	}
	
	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {
		Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
		if(backlog == null) {
			throw new ProjectNotFoundException("Project with ID '"+ backlog_id +"' does not exist");		
		}
		
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if(projectTask == null ) {
			throw new ProjectNotFoundException("Project Task '"+ pt_id +"' not Found");		
		}
		
		if(!projectTask.getProjectIdentifier().equals(backlog_id)) {
			throw new ProjectNotFoundException("Project Task '"+ pt_id +"' does not exist in project: '"+ backlog_id);
		}
		
		return projectTaskRepository.findByProjectSequence(pt_id);
	}
	
	public ProjectTask updateByProjectSequence(ProjectTask updateTask, String backlog_id, String pt_id) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
		
		projectTask = updateTask;
		return projectTaskRepository.save(projectTask);
	}
	
	public void deletePTByProjectSequence(String backlog_id, String pt_id) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);				
		projectTaskRepository.delete(projectTask);		
	}
}
