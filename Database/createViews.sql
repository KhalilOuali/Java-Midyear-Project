-- Active: 1670016658658@@localhost@3306@research_center

CREATE VIEW researcherInfo AS
SELECT 
	researcher_id "Researcher ID", 
	full_name "Full Name", 
	experiment_id "Experiment ID", 
	login "Login", 
	password "Password"
FROM personnel 
INNER JOIN researcher 
ON researcher_id = personnel_id
ORDER BY researcher_id;
