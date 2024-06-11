select * from person;
select count(*) from person;

--

select * from person where has_been_processed = true;
select count(*) from person where has_been_processed = true;

--

select * from person where has_been_processed = false;
select count(*) from person where has_been_processed = false;

--

drop table person;
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
