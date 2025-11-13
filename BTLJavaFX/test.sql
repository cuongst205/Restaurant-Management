SELECT
    t.id_employee,
    COUNT(DISTINCT strftime('%Y-%m-%d', t.time_login)) AS workdays_count
FROM time_sheet AS t join employee AS e ON e.id_employee = t.id_employee
WHERE strftime('%Y', t.time_login) = strftime('%Y', 'now')
  AND strftime('%m', t.time_login) = strftime('%m', 'now')
 and e.role = 'Staff'
GROUP BY t.id_employee