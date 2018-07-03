-- 1. Write MySQL query to find IPs that mode more than a certain number of requests for a given time period.

select ip, count(ip) as request_number from log_tb
where log_date between '2017-01-01 13:00:00.000' and '2017-01-01 14:00:00.000'
group by ip
having count(ip) > 100;

-- 2. Write MySQL query to find requests made by a given IP.
select * from log_tb
where ip = '192.168.228.188';