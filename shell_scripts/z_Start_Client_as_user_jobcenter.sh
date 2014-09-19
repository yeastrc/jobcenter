#!/bin/sh


PATH_TO_JOBCENTER_CLIENT=

if [ "$PATH_TO_JOBCENTER_CLIENT" = "" ]; then

       echo  PATH_TO_JOBCENTER_CLIENT is empty, it must be set before running this command

  exit 1
fi

echo PATH_TO_JOBCENTER_CLIENT = $PATH_TO_JOBCENTER_CLIENT

su -c "$PATH_TO_JOBCENTER_CLIENT/runJobCenterClient.sh" -s /bin/bash jobcenter

