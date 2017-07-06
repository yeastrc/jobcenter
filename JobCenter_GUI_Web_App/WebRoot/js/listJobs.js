/**
 * listJobs.js
 * 
 * Javascript for the page listJobs.jsp 
 * 
 * page variable listJobs
 * 
 */

//////////////////////////////////
//JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


///////////////
$(document).ready(function() {
	try {
		listJobs.init();
	} catch( e ) {
//		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});

/**
 * Javascript for the page listJobs.jsp 
 * 
 * @constructor
 * 
 * 
 */
var ListJobs = function() {
	
	var _selector_all_optionHTML = null;
	var _job_type_selector_name_templateTemplate = null;
	var _request_type_selector_name_templateTemplate = null;
	var _job_entry_templateTemplate = null;
	
	/**
	 * Init
	 * 
	 * @param {string} uid The UID of the protein bar
	 * @returns {object} { hex:hex code, opacity:#}
	 * 
	 */
	this.init = function() {
		var objectThis = this;
		
		var $selector_all_option = $("#selector_all_option");
		_selector_all_optionHTML = $selector_all_option.html();
		
		var $job_type_selector_name_template = $("#job_type_selector_name_template");
		if ( $job_type_selector_name_template.length === 0 ) {
			throw Error("Element with id='job_type_selector_name_template' not found");
		}
		var job_type_selector_name_templateContents = $job_type_selector_name_template.html();
		_job_type_selector_name_templateTemplate = Handlebars.compile( job_type_selector_name_templateContents );
		
		var $request_type_selector_name_template = $("#request_type_selector_name_template");
		if ( $request_type_selector_name_template.length === 0 ) {
			throw Error("Element with id='request_type_selector_name_template' not found");
		}
		var request_type_selector_name_templateContents = $request_type_selector_name_template.html();
		_request_type_selector_name_templateTemplate = Handlebars.compile( request_type_selector_name_templateContents );

		var $job_entry_template = $("#job_entry_template");
		if ( $job_entry_template.length === 0 ) {
			throw Error("Element with id='job_entry_template' not found");
		}
		var job_entry_templateContents = $job_entry_template.html();
		_job_entry_templateTemplate = Handlebars.compile( job_entry_templateContents );

		
		this.populateJobTypeSelectorActual();
		this.populateRequestTypeSelectorActual();
		
		this.populateJobTypeSelector();
		this.populateRequestTypeSelector();
		
		this.populateJobList();
		
		var $update_list_button = $("#update_list_button");
		$update_list_button.click( function() {
			objectThis.populateJobList();
		});
		
		var $clear_statuses_button = $("#clear_statuses_button");
		$clear_statuses_button.click( function() {
			var $status_selector_entry_jqAll = $(".status_selector_entry_jq");
			$status_selector_entry_jqAll.prop( "checked", false ); 			
		});
	};
	
	/**
	 * 
	 * 
	 */
	this.populateJobTypeSelector = function() {
		var objectThis = this;
		var requestData = {
		};
		var _URL = contextPathJSVar + "/services/getFromJCServer/jobTypesListAll";
		// var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				try {
					objectThis.populateJobTypeSelectorProcessAjaxResponse( { responseData : data, requestData : requestData } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
				try {
					handleAJAXFailure( errMsg );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
	        },
	        error : function(jqXHR, textStatus, errorThrown) {
				try {
					handleAJAXError(jqXHR, textStatus, errorThrown);
					// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
					// textStatus: " + textStatus );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
		});
	};
	
	/**
	 * 
	 * 
	 */
	this.populateJobTypeSelectorProcessAjaxResponse = function( params ) {
		var responseData = params.responseData;
		var requestData = params.requestData;
		var jobTypes = responseData.jobTypes;
		
		this.populateJobTypeSelectorActual( { jobTypes : jobTypes } );
	};
	
	/**
	 * 
	 * 
	 */
	this.populateJobTypeSelectorActual = function( params ) {
		var jobTypes;
		if ( params ) {
			jobTypes = params.jobTypes;
		}

		var $job_type_selector = $("#job_type_selector");
		
		if ( jobTypes ) {
			jobTypes.forEach( function ( currentArrayValue, index, array ) {
				var jobType = currentArrayValue;
				var html = _job_type_selector_name_templateTemplate( jobType );
				$( html ).appendTo( $job_type_selector );
			}, this /* passed to function as this */ );
		} else {
			//  If no jobTypes, populate a "All" with value ""
			$( _selector_all_optionHTML ).appendTo( $job_type_selector );
		}
	};

	/**
	 * 
	 * 
	 */
	this.populateRequestTypeSelector = function() {
		var objectThis = this;
		var requestData = {
		};
		var _URL = contextPathJSVar + "/services/getFromJCServer/requestTypesListAll";
		// var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				try {
					objectThis.populateRequestTypeSelectorProcessAjaxResponse( { responseData : data, requestData : requestData } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
				try {
					handleAJAXFailure( errMsg );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
	        },
	        error : function(jqXHR, textStatus, errorThrown) {
				try {
					handleAJAXError(jqXHR, textStatus, errorThrown);
					// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
					// textStatus: " + textStatus );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
		});
	};
	
	/**
	 * 
	 * 
	 */
	this.populateRequestTypeSelectorProcessAjaxResponse = function( params ) {
		var responseData = params.responseData;
		var requestData = params.requestData;
		var requestTypes = responseData.requestTypes;
		
		this.populateRequestTypeSelectorActual( { requestTypes : requestTypes } );
	};
	
	/**
	 * 
	 * 
	 */
	this.populateRequestTypeSelectorActual = function( params ) {
		var requestTypes;
		if ( params ) {
			requestTypes = params.requestTypes;
		}
		
		var $request_type_selector = $("#request_type_selector");
		
		if ( requestTypes ) {
			requestTypes.forEach( function ( currentArrayValue, index, array ) {
				var requestType = currentArrayValue;
				var html = _request_type_selector_name_templateTemplate( requestType );
				$( html ).appendTo( $request_type_selector );
			}, this /* passed to function as this */ );
		} else {
			//  If no jobTypes, populate a "All" with value ""
			$( _selector_all_optionHTML ).appendTo( $request_type_selector );
		}
	};

	

	/**
	 * 
	 * 
	 */
	this.populateJobList = function() {
		var objectThis = this;

		var selectedStatusIds = [];
		var $status_selector_entry_jqAll = $(".status_selector_entry_jq");
		$status_selector_entry_jqAll.each( function( index, element ) {
			var $status_selector_entry_jq = $( this );
			if ( $status_selector_entry_jq.prop( "checked" ) ) {
				var status_id = $status_selector_entry_jq.attr("data-status_id");
				selectedStatusIds.push( status_id );
			}
		});
		
		var $job_type_selector = $("#job_type_selector");
		var job_type_selectorVal = $job_type_selector.val();
		if ( job_type_selectorVal === "" ) {
			job_type_selectorVal = undefined // clear if "All" is selected
		}
	
		var $request_type_selector = $("#request_type_selector");
		var request_type_selectorVal = $request_type_selector.val();
		if ( request_type_selectorVal === "" ) {
			request_type_selectorVal = undefined // clear if "All" is selected
		}
		
		var $submitter = $("#submitter");
		var submitter = $submitter.val();
		if ( $submitter.length === 0 || submitter === "" ) {
			submitter = undefined // clear if no value is entered
		}
		
		var requestData = {
				statusId : selectedStatusIds,
				jobTypeName : job_type_selectorVal,
				requestTypeName : request_type_selectorVal,
				submitter : submitter
		};
		var _URL = contextPathJSVar + "/services/getFromJCServer/listJobs";
		// var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			dataType : "json",
			traditional: true,  //  Force traditional serialization of the data sent
			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
			//   So any array is passed as "<object property name>=<value>" which is what Jersey expects
			success : function(data) {
				try {
					objectThis.populateJobListProcessAjaxResponse( { responseData : data, requestData : requestData } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
				try {
					handleAJAXFailure( errMsg );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
	        },
	        error : function(jqXHR, textStatus, errorThrown) {
				try {
					handleAJAXError(jqXHR, textStatus, errorThrown);
					// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
					// textStatus: " + textStatus );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
		});
	};
	
	/**
	 * 
	 * 
	 */
	this.populateJobListProcessAjaxResponse = function( params ) {
		var objectThis = this;
		var responseData = params.responseData;
		var requestData = params.requestData;
		
		var jobList = responseData.jobList;
		var jobCountTotal = responseData.jobCountTotal;
		
		var $job_count_display = $("#job_count_display");
		$job_count_display.text( jobCountTotal );
		
		var $job_list = $("#job_list");
		
		$job_list.empty();
		
		if ( jobList ) {
			jobList.forEach( function ( currentArrayValue, index, array ) {
				var job = currentArrayValue;
				var html = _job_entry_templateTemplate( job );
				$( html ).appendTo( $job_list );
			}, this /* passed to function as this */ );
		} else {
		}
		
		this.addClickHandlersToJobs( { $container : $job_list } );
		
	};


	/**
	 * 
	 * 
	 */
	this.requeueJob = function( params ) {
		var clickedThis = params.clickedThis;
		
		var $clickedThis = $( clickedThis );  // clicked link
		
		var job_id = $clickedThis.attr( "data-job_id" );
		var job_record_version_id = $clickedThis.attr( "data-job_record_version_id" );
		
		var objectThis = this;
		var requestData = {
				job_id : job_id,
				job_record_version_id : job_record_version_id
		};
		var _URL = contextPathJSVar + "/services/postToJCServer/requeueJob";
		// var request =
		$.ajax({
			type : "POST",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				try {
					objectThis.requeueJobProcessAjaxResponse( { responseData : data, requestData : requestData, clickedThis : clickedThis, job_id : job_id } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
				try {
					handleAJAXFailure( errMsg );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
	        },
	        error : function(jqXHR, textStatus, errorThrown) {
				try {
					handleAJAXError(jqXHR, textStatus, errorThrown);
					// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
					// textStatus: " + textStatus );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
		});
		
	};
	
	this.requeueJobProcessAjaxResponse = function( params ) {
		var objectThis = this;
		var responseData = params.responseData;
		var requestData = params.requestData;
		var clickedThis = params.clickedThis;
		var job_id = params.job_id;

		var $clickedThis = $( clickedThis );  // clicked link

		//  Get topmost element with class single_job_entry_jq.  
		//  Done since a replacement of the job is done inside of div 
		var $single_job_entry_jq = $clickedThis.closest(".single_job_entry_jq"); // job enclosing div
		var prev_$single_job_entry_jq = $single_job_entry_jq;
		while ( $single_job_entry_jq.length !== 0 ) {
			prev_$single_job_entry_jq = $single_job_entry_jq;
			//  Don't use .closest() since first evaluates the element itself.
			$single_job_entry_jq = $single_job_entry_jq.parent(".single_job_entry_jq"); // job enclosing div
		}
		$single_job_entry_jq = prev_$single_job_entry_jq;
		
		if ( ! responseData.statusSuccess ) {
			
			alert( "requeue failed" )
		} else {
			alert( "requeue successful" );
		}

		this.refreshJobInJobList( { jobId : job_id, $single_job_entry_jq : $single_job_entry_jq } );
		
	}
	
	


	/**
	 * 
	 * 
	 */
	this.cancelJob = function( params ) {
		var clickedThis = params.clickedThis;
		
		var $clickedThis = $( clickedThis );  // clicked link
		
		var job_id = $clickedThis.attr( "data-job_id" );
		var job_record_version_id = $clickedThis.attr( "data-job_record_version_id" );
		
		var objectThis = this;
		var requestData = {
				job_id : job_id,
				job_record_version_id : job_record_version_id
		};
		var _URL = contextPathJSVar + "/services/postToJCServer/cancelJob";
		// var request =
		$.ajax({
			type : "POST",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				try {
					objectThis.cancelJobProcessAjaxResponse( { responseData : data, requestData : requestData, clickedThis : clickedThis, job_id : job_id } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
				try {
					handleAJAXFailure( errMsg );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
	        },
	        error : function(jqXHR, textStatus, errorThrown) {
				try {
					handleAJAXError(jqXHR, textStatus, errorThrown);
					// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
					// textStatus: " + textStatus );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
		});
		
	};
	
	this.cancelJobProcessAjaxResponse = function( params ) {
		var objectThis = this;
		var responseData = params.responseData;
		var requestData = params.requestData;
		var clickedThis = params.clickedThis;
		var job_id = params.job_id;

		var $clickedThis = $( clickedThis );  // clicked link

		//  Get topmost element with class single_job_entry_jq.  
		//  Done since a replacement of the job is done inside of div 
		var $single_job_entry_jq = $clickedThis.closest(".single_job_entry_jq"); // job enclosing div
		var prev_$single_job_entry_jq = $single_job_entry_jq;
		while ( $single_job_entry_jq.length !== 0 ) {
			prev_$single_job_entry_jq = $single_job_entry_jq;
			//  Don't use .closest() since first evaluates the element itself.
			$single_job_entry_jq = $single_job_entry_jq.parent(".single_job_entry_jq"); // job enclosing div
		}
		$single_job_entry_jq = prev_$single_job_entry_jq;
		
		if ( ! responseData.statusSuccess ) {
			
			alert( "cancel failed" )
		} else {
			alert( "cancel successful" );
		}

		this.refreshJobInJobList( { jobId : job_id, $single_job_entry_jq : $single_job_entry_jq } );
		
	}
	
	/**
	 * 
	 * 
	 */
	this.refreshJobInJobList = function( params ) {
		var objectThis = this;
		var jobId = params.jobId;
		var $single_job_entry_jq = params.$single_job_entry_jq;
		
		var requestData = {
				job_id : jobId
		};
		var _URL = contextPathJSVar + "/services/getFromJCServer/getJobForGUIForJobList";
		// var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				try {
					objectThis.refreshJobInJobListProcessAjaxResponse( { responseData : data, requestData : requestData, $single_job_entry_jq : $single_job_entry_jq } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
				try {
					handleAJAXFailure( errMsg );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
	        },
	        error : function(jqXHR, textStatus, errorThrown) {
				try {
					handleAJAXError(jqXHR, textStatus, errorThrown);
					// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
					// textStatus: " + textStatus );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
		});
	};

	/**
	 * 
	 * 
	 */
	this.refreshJobInJobListProcessAjaxResponse = function( params ) {
		var objectThis = this;
		var responseData = params.responseData;
		var requestData = params.requestData;
		var $single_job_entry_jq = params.$single_job_entry_jq;

		var job = responseData.job;
		
		if ( ! job ) {
			alert( "job not found for refresh" );
			return;
		}
		
		$single_job_entry_jq.empty();
		
		var html = _job_entry_templateTemplate( job );
		$( html ).appendTo( $single_job_entry_jq );

		this.addClickHandlersToJobs( { $container : $single_job_entry_jq } );
	};

	/**
	 * 
	 * 
	 */
	this.addClickHandlersToJobs = function( params ) {
		var objectThis = this;
		var $container = params.$container;
		
		var $requeue_job_link_jqAll = $container.find(".requeue_job_link_jq");
		$requeue_job_link_jqAll.click( function( event ) { 
			objectThis.requeueJob( { clickedThis : this } ); 
			event.preventDefault();
		});
		
		var $cancel_job_link_jqAll = $container.find(".cancel_job_link_jq");
		$cancel_job_link_jqAll.click( function( event ) { 
			objectThis.cancelJob( { clickedThis : this } ); 
			event.preventDefault();
		});
	}
	

	
};

var listJobs = new ListJobs();

