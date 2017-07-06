/**
 * viewJob.js
 * 
 * Javascript for the page viewJob.jsp 
 * 
 * page variable viewJob
 * 
 */

//////////////////////////////////
//JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


///////////////
$(document).ready(function() {
	try {
		viewJob.init();
	} catch( e ) {
//		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});

/**
 * Javascript for the page viewJob.jsp 
 * 
 * @constructor
 * 
 * 
 */
var ViewJob = function() {
	
	var _job_id = null;
	var _job_record_version_id = null;

	var _job_parameter_templateTemplate = null;
	var _run_entry_templateTemplate = null;
	
	/**
	 * Init
	 * 
	 * @param {string} uid The UID of the protein bar
	 * @returns {object} { hex:hex code, opacity:#}
	 * 
	 */
	this.init = function() {
		var objectThis = this;
		
		_job_id = window.location.hash.substr(1);
		
		var $job_parameter_template = $("#job_parameter_template");
		if ( $job_parameter_template.length === 0 ) {
			throw Error("Element with id='job_parameter_template' not found");
		}
		var job_parameter_templateContents = $job_parameter_template.html();
		_job_parameter_templateTemplate = Handlebars.compile( job_parameter_templateContents );

		

		var $run_entry_template = $("#run_entry_template");
		if ( $run_entry_template.length === 0 ) {
			throw Error("Element with id='run_entry_template' not found");
		}
		var run_entry_templateContents = $run_entry_template.html();
		_run_entry_templateTemplate = Handlebars.compile( run_entry_templateContents );

		var $requeue_link = $("#requeue_link");
		$requeue_link.click( function( event ) { 
			objectThis.requeueJob(); 
			event.preventDefault();
		});

		var $cancel_link = $("#cancel_link");
		$cancel_link.click( function( event ) { 
			objectThis.cancelJob(); 
			event.preventDefault();
		});

		
		this.updateJobOnPage();
	};
	
	/**
	 * 
	 * 
	 */
	this.updateJobOnPage = function( params ) {
		var objectThis = this;
		
		var requestData = {
				job_id : _job_id
		};
		var _URL = contextPathJSVar + "/services/getFromJCServer/getJob";
		// var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				try {
					objectThis.updateJobOnPageProcessAjaxResponse( { responseData : data, requestData : requestData } );
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
	this.updateJobOnPageProcessAjaxResponse = function( params ) {
		var objectThis = this;
		var responseData = params.responseData;
		var requestData = params.requestData;

		var job = responseData.job;
		
		if ( ! job ) {
			alert( "job not found" );
			return;
		}
		
		_job_record_version_id = job.dbRecordVersionNumber;
		
		var $job_type_name = $("#job_type_name");
		$job_type_name.text( job.jobTypeName );
		var $job_status = $("#job_status");
		$job_status.text( job.status );
		
		var $job_id = $("#job_id");
		$job_id.text( job.jobId );
		var $job_priority = $("#job_priority");
		$job_priority.text( job.priority );
		var $job_submitter = $("#job_submitter");
		$job_submitter.text( job.submitter );
		var $job_submit_date = $("#job_submit_date");
		$job_submit_date.text( job.submitDate );

		var $requeue_job_block = $("#requeue_job_block");
		if ( job.requeueable ) {
			$requeue_job_block.show();
		} else {
			$requeue_job_block.hide();
		}
		var $cancel_job_block = $("#cancel_job_block");
		if ( job.cancellable ) {
			$cancel_job_block.show();
		} else {
			$cancel_job_block.hide();
		}

		var $job_parameter_table = $("#job_parameter_table");
		$job_parameter_table.empty();
		if ( job.jobParameterList ) {
			job.jobParameterList.forEach( function ( currentArrayValue, index, array ) {
				var jobParameter = currentArrayValue;
				var html = _job_parameter_templateTemplate( jobParameter );
				$( html ).appendTo( $job_parameter_table );
			}, this /* passed to function as this */ );
		}
		

		var $run_list_table__tbody = $("#run_list_table tbody");
		$run_list_table__tbody.empty();
		if ( job.runForGUIList ) {
			job.runForGUIList.forEach( function ( currentArrayValue, index, array ) {
				var run = currentArrayValue;
				var html = _run_entry_templateTemplate( run );
				( $run_list_table__tbody ).append( html );
			}, this /* passed to function as this */ );
		}
//
//		private int jobId;
//		private String jobTypeName;
//		private int requestId;
//		private String submitDate;
//		private String submitter;
//		private int priority;
//		private Integer requiredExecutionThreads;
//		private String status;
//		private Integer dbRecordVersionNumber;
	};
	
	/**
	 * 
	 * 
	 */
	this.requeueJob = function( params ) {
		var objectThis = this;
		var requestData = {
				job_id : _job_id,
				job_record_version_id : _job_record_version_id
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
		
		if ( ! responseData.statusSuccess ) {
			
			alert( "requeue failed" )
		} else {
			alert( "requeue successful" );
		}

		this.updateJobOnPage();
	};

	/**
	 * 
	 * 
	 */
	this.cancelJob = function( params ) {
		var objectThis = this;
		var requestData = {
				job_id : _job_id,
				job_record_version_id : _job_record_version_id
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
					objectThis.cancelJobProcessAjaxResponse( { responseData : data, requestData : requestData } );
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

		var _job_id = null;
		var _job_record_version_id = null;
		
		if ( ! responseData.statusSuccess ) {
			
			alert( "cancel failed" )
		} else {
			alert( "cancel successful" );
		}

		this.updateJobOnPage();
		
	}
	



	
};

var viewJob = new ViewJob();

