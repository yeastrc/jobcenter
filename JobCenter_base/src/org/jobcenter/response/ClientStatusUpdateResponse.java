package org.jobcenter.response;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.nondbdto.ClientIdentifierDTO;

/**
 * The response to a client status update
 *
 */
@XmlRootElement(name = "clientStatusUpdateResponse")

public class ClientStatusUpdateResponse extends BaseResponse {


}
