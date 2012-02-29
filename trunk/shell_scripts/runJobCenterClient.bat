


REM   !!!  Warning, this file will be overlaid if the job manager client is re-installed in the same directory.

REM     If any changes are needed to this file, copy this file to another name and make the changes there.


java   -Xmx128m -jar jobcenter_client_code/client_root/main_jar/jobcenter_client_root.jar  > out.txt

REM   default -Xmx64m

REM       add   -DtestEnv=Y   and/or  -DdevEnv=Y   as needed for testing