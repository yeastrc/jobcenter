package org.jobcenter.jdbc;

import java.util.List;
import org.jobcenter.dto.RequestTypeDTO;

public interface RequestTypeJDBCDAO {

	public  List<RequestTypeDTO> getRequestTypes( );

}
