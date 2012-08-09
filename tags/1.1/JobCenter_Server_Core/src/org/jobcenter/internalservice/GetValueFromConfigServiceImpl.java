package org.jobcenter.internalservice;

import java.util.List;

import org.apache.log4j.Logger;

import org.jobcenter.dao.*;
import org.jobcenter.dtoserver.ConfigSystemDTO;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper database roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.


/**
*
*
*/

//  Spring database transaction demarcation.
//  Spring will start a database transaction when any method is called and call commit when it completed
//    or call roll back if an unchecked exception is thrown.
@Transactional ( propagation = Propagation.REQUIRED, readOnly = true )

public class GetValueFromConfigServiceImpl implements GetValueFromConfigService {

	private static Logger log = Logger.getLogger(GetValueFromConfigServiceImpl.class);


	public static GetValueFromConfigService getFromApplicationContext(ApplicationContext ctx) {
		return (GetValueFromConfigService) ctx.getBean("getValueFromConfigService");
	}

	//  Service


	//  DAO

	private ConfigSystemDAO configSystemDAO;


	//  JDBCDAO



	/* (non-Javadoc)
	 * @see org.jobcenter.service.GetValueFromConfigService#getConfigValueAsInteger(java.lang.String)
	 */
	@Override
	public Integer getConfigValueAsInteger( String configKey ) {


		Integer configValue = null;

		String configValueAsString = getConfigValueAsString( configKey );

		try {

			int queryResponseValueInt = Integer.parseInt( configValueAsString );

			configValue = queryResponseValueInt;


		} catch ( Throwable t ) {

			log.error( "retrieving '" + configKey + "' from database resulted in value '" + configValueAsString + "' which could not be parsed to an integer."  );
		}

		return configValue;
	}



	/* (non-Javadoc)
	 * @see org.jobcenter.service.GetValueFromConfigService#getConfigValueAsString(java.lang.String)
	 */
	@Override
	public String getConfigValueAsString( String configKey ) {


		String configValue = null;

		try {
			List queryResponse = null;

			try {
				queryResponse = configSystemDAO.findByConfigKey( configKey );

			} catch ( Throwable t ) {

				log.error( "Exception retrieving '" + configKey + "' from database. Exception: " + t.toString(), t  );

				throw t;
			}

			if ( queryResponse != null && ( ! queryResponse.isEmpty() ) ) {

				Object queryResponseValueObject = null;

				try {

					queryResponseValueObject = queryResponse.get( 0 );

					ConfigSystemDTO configSystemDTO = (ConfigSystemDTO) queryResponseValueObject;

					configValue = configSystemDTO.getConfigValue();


					if ( queryResponse.size() > 1 ) {

						log.error( "retrieving '" + configKey + "' from database retrieved more than one record. Using first record.  Value = '" + configValue + "'."  );
					}


				} catch ( Throwable t ) {

					log.error( "retrieving '" + configKey + "' from database resulted in value '" + queryResponseValueObject + "' which could not be processed."  );
				}

			} else {


				log.error( "retrieving '" + configKey + "' from database retrieved no data."  );
			}


		} catch ( Throwable t ) {


		}

		return configValue;
	}





	public ConfigSystemDAO getConfigSystemDAO() {
		return configSystemDAO;
	}
	public void setConfigSystemDAO(ConfigSystemDAO configSystemDAO) {
		this.configSystemDAO = configSystemDAO;
	}




}
