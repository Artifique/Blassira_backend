UPDATE incident_reports SET severity = 'LOW' WHERE severity IS NULL OR severity NOT IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');
