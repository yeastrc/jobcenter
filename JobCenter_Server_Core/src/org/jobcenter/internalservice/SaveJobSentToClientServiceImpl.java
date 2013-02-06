package org.jobcenter.internalservice;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.constants.Constants;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dao.JobSentToClientDAO;
import org.jobcenter.dao.NodeClientStatusDAO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobSentToClient;
import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.dtoservernondb.NodeClientStatusDTOPrevCurrent;
import org.jobcenter.request.ClientStartupRequest;
import org.jobcenter.request.ClientStatusUpdateRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper database roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//      Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//               Otherwise it commits the transaction.


/**
* Inserts to the job_sent_to_client
*
*/

//Spring database transaction demarcation.
//Spring will start a database transaction when any method is called and call commit when it completed
//  or call roll back if an unchecked exception is thrown.
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class SaveJobSentToClientServiceImpl implements SaveJobSentToClientService  {

	private static Logger log = Logger.getLogger(SaveJobSentToClientServiceImpl.class);


	//  Service


	//  DAO

	private JobSentToClientDAO jobSentToClientDAO;


	public JobSentToClientDAO getJobSentToClientDAO() {
		return jobSentToClientDAO;
	}
	public void setJobSentToClientDAO(JobSentToClientDAO jobSentToClientDAO) {
		this.jobSentToClientDAO = jobSentToClientDAO;
	}

	
	
	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SaveJobSentToClientService#saveJobSentToClient(org.jobcenter.dto.Job)
	 */
	@Override
	public void saveJobSentToClient( Job job ) {

		JobSentToClient jobSentToClient = new JobSentToClient();
		
		jobSentToClient.setJobId( job.getId() );
		jobSentToClient.setRunId( job.getCurrentRunId() );
		jobSentToClient.setJobParameterCountWhenJobSubmitted( job.getJobParameterCount() );
		jobSentToClient.setJobParameterCountWhenJobRetrieved( job.getJobParameterCountWhenRetrievedByGetJob() );

		try {
			JAXBContext jc = JAXBContext.newInstance( Constants.DTO_PACKAGE_PATH, this.getClass().getClassLoader() );

			Marshaller marshaller = jc.createMarshaller();

			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			
			marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
			
			ByteArrayOutputStream os = new ByteArrayOutputStream( 10000 );

			marshaller.marshal( job, os );

			String xmlMarshalledJob = os.toString( "UTF-8" );
			
			jobSentToClient.setXmlMarshalledJob( xmlMarshalledJob );

			jobSentToClientDAO.save( jobSentToClient );

		} catch ( Throwable t ) {

			String msg = "Exception in saveJobSentToClientService. job id = " + job.getId();

			log.error( msg );

			throw new RuntimeException( msg, t );
		}
	}

}
