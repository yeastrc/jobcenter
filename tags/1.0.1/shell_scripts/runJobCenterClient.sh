#!/bin/sh

#  !!!  Warning, this file will be overlaid if the job manager client is re-installed in the same directory.

#     If any changes are needed to this file, copy this file to another name and make the changes there.

#  base_dir = directory this application was started in - helps when looking at jobs in "ps" command

java  -Xmx128m -Dbase_dir=`pwd`    -jar jobcenter_client_code/client_root/main_jar/jobcenter_client_root.jar  > out.txt

#  default -Xmx64m

#   add   -DtestEnv=Y   and/or  -DdevEnv=Y   as needed for testing