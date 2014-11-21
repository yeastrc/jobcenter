

export ANT_OPTS="-Xmx1024m -XX:MaxPermSize=512m"

ant -f ant_build_all_JobCenter.xml > ZZ_ant_out.txt 2>&1 

