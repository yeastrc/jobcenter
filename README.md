# jobcenter
### A client-server application and framework for job management and distributed job execution

JobCenter is a client-server application and framework for job management and distributed job execution. The client and server components are both written in Java and are cross-platform and relatively easy to install. All communication with the server is client-driven, which allows worker nodes to run anywhere (even behind external firewalls or “in the cloud”) and provides inherent load balancing. Adding a worker node to the worker pool is as simple as dropping the JobCenter client files onto any computer and performing basic configuration, which provides tremendous ease-of-use, flexibility, and limitless horizontal scalability. Each worker installation may be independently configured, including the types of jobs it is able to run. Executed jobs may be written in any language and may include multistep workflows.

Please see the file '`docs/Limitations_on_Values_passed_through_Jobcenter_Framework.txt`' for information specifically about passing values through the Jobcenter framework.  The limitations on the values applies since the first version of Jobcenter. 

Breaking change  version 1.7 and current trunk:

In the Jobcenter client, calls the module makes on the `ModuleInterfaceResponse` response object to add run messages and run output parameters will throw checked exceptions 
if the Strings passed cannot be marshaled and unmarshaled as UTF-8 XML.

see '`docs/Release_History.txt`' for more info
