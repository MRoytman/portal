##################### GET THE PARAM FROM THE CALLER
appName=$1
cd ..
currentPath="$PWD"   # Path = xxx/MSFForm/deployfx

cd ..
deployPath="$PWD"  # Path = xxx/MSFForm

cd ..
rootPath="$PWD"  # Path = /var/lib/tomcat7/webapps

##################### CREATE LOG FILES
mkdir ftpmsf # This is public ftp
mkdir ftpmsf/logs
cd ftpmsf/logs
now=$(date +"%Y_%m_%d___%H_%M")
logpath="$PWD"/$appName"___"$now.txt

echo " " > $logpath 
echo "--------------------------------------------" >> $logpath 
echo " " >> $logpath 
echo ":::: START THE GENERATION OF "$appName", AT "$(date +"%T %m-%d-%Y") >> $logpath 
echo " " >> $logpath 
echo "::Root Path = "$rootPath >> $logpath 
echo "::Deploy Path = "$deployPath >> $logpath 
echo "::Current Path = "$currentPath >> $logpath 
echo " " >> $logpath 
echo "--------------------------------------------" >> $logpath 

cd $currentPath
mkdir $appName
mkdir $appName"_backup"
mkdir $appName"_backup"/resources
echo "Created Project folders: "$appName  >> $logpath
cp resources/* $appName"_backup"/resources/
cp -r resources $appName/ 

mkdir AGG_OPD_XX/src/main/resources
cp resources/* AGG_OPD_XX/src/main/resources/
echo "Copy resource files to the project folder ... " >> $logpath

##################### START COMPILE COMMON PROJECTS
echo "--------------------------------------------" >> $logpath 
echo "---- START MAVEN BUILD ---------" >> $logpath 
echo "--------------------------------------------" >> $logpath 

cd $deployPath/deploy/MsfApps
echo "Compiling MsfApps ..."  >> $logpath
mvn install  >> $logpath

echo "Compiling CommonIpdApps ..."  >> $logpath
cd $deployPath/deploy/CommonIpdApps
mvn install  >> $logpath

cd $deployPath/deploy/AggregatedApps
echo "Compiling AggregateApps ..."  >> $logpath
mvn install  >> $logpath
##################### END COMPILE COMMON PROJECTS, Now main Project

cd $currentPath
echo "Compiling "$appName" ..."  >> $logpath
cd AGG_OPD_XX
mvn clean install -P entryformprofile  >> $logpath
mvn install -P adminprofile >> $logpath

echo "--------------------------------------------" >> $logpath 
echo "---- FINISHED MAVEN BUILD ---------" >> $logpath 
echo "--------------------------------------------" >> $logpath 

##################### RENAME JAR FILES AND MOVE TO THE PROJECT FOLDER
cd target
mv AGG_OPD_XXAdmin1.4.jar $appName"Admin1.4.jar"
mv AGG_OPD_XXEntryForm3.0.jar $appName"EntryForm3.0.jar"
echo "Rename Jar Files before copying ..." >> $logpath  

cd $currentPath
mv AGG_OPD_XX/target/$appName"Admin1.4.jar" AGG_OPD_XX/target/$appName"EntryForm3.0.jar" $appName"_backup"/
echo "Moved Jar files to "$appName"_backup" >> $logpath   

##################### CLEAN UP RESOURCE FILES
echo "Clean up target and resources files ..."  >> $logpath

rm -r AGG_OPD_XX/target
rm AGG_OPD_XX/src/main/resources/*
rm resources/*

##################### START TO SIGN AND PACKING FILES
cd $currentPath/$appName"_backup"
cp $deployPath/deploy/myKeystore $currentPath/$appName"_backup"/
echo "Create local ftpmsf folder ..."  >> $logpath
mkdir ftpmsf # create local ftp inside the folder deployfX
echo "--------------------------------------------" >> $logpath 
echo "---- START TO SIGN AND PACK ---------" >> $logpath 
echo "--------------------------------------------" >> $logpath 

echo "JavaHome = "$JAVA_HOME  >> $logpath
echo "Packing..."$appName"EntryForm3.0.jar" >> $logpath
pack200 --strip-debug --segment-limit=-1 --repack $appName"EntryForm3.0.jar" >> $logpath

#/usr/local/java/jdk1.7.0_25/bin/jarsigner #### if jarsigner not found, put the complete path!
echo "Signing..."$appName"EntryForm3.0.jar"  >> $logpath
jarsigner -storetype pkcs12 -keystore myKeystore -storepass msfgeneva $appName"EntryForm3.0.jar" myself  >> $logpath

echo "Packing..."$appName"EntryForm3.0.jar.pack.gz" >> $logpath
pack200 --segment-limit=-1 $appName"EntryForm3.0.jar.pack.gz" $appName"EntryForm3.0.jar"  >> $logpath

echo "Signing..."$appName"Admin1.4.jar"  >> $logpath
jarsigner -storetype pkcs12 -keystore myKeystore -storepass msfgeneva $appName"Admin1.4.jar" myself  >> $logpath

echo "--------------------------------------------" >> $logpath 
echo "---- FINISHED SIGNING AND PACKED ---------" >> $logpath 
echo "--------------------------------------------" >> $logpath 

##################### MOVE FILES INTO PROJECT FOLDERS, THEN TO THE GLOBAL FTPMSF FOLDER
mv *EntryForm* *Admin*.jar $currentPath/ftpmsf/$appName/
rm -r $currentPath/ftpmsf/$appName/resources
mv $currentPath/$appName"_backup"/resources $currentPath/ftpmsf/$appName/

echo "Copied jar files into local ftp folder "$appName  >> $logpath

cp $deployPath/deploy/commonAggregate/* $currentPath/ftpmsf/$appName/
echo "Copied common zip files into ftp "$appName/ >> $logpath

echo "Move the folder appName from local FTP to public FTP"  >> $logpath 
rm -r $rootPath/ftpmsf/$appName
mv $currentPath/ftpmsf/$appName $rootPath/ftpmsf

echo "--------------------------------------------" >> $logpath 
echo " " >> $logpath 
echo ":::: FINISHED SUCCESSFULLY THE GENERATION OF "$appName" AT "$(date +"%T %m-%d-%Y") >> $logpath 
echo " " >> $logpath 
echo "--------------------------------------------" >> $logpath 
