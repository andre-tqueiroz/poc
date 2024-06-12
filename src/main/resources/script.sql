select * from raw_data;
select count(*) from raw_data;

--

select * from person;
select count(*) from person;

--

drop table person;
drop table raw_data;
drop table batch_step_execution_context;
drop table batch_step_execution;
drop table batch_job_execution_params;
drop table batch_job_execution_context;
drop table batch_job_execution;
drop table batch_job_instance;

--

select * from batch_step_execution_context;
select * from batch_step_execution;
select * from batch_job_execution_params;
select * from batch_job_execution_context;
select * from batch_job_execution;
select * from batch_job_instance;
