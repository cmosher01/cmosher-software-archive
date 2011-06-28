SELECT
	ssiEmployeeID pk,
    lastName,
	firstName,
	name department
FROM
    DA_Prod.SSIEmployee
    INNER JOIN DA_Prod.SSIDepartment on SSIDepartment.ssiDepartmentID = SSIEmployee.ssiDepartmentID
WHERE
    bResigned = 'F'
ORDER BY
    lastName,
	firstName
