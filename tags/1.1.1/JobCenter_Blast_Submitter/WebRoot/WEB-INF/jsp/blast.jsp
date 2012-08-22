<%@ include file="/WEB-INF/includes/imports.inc"%>

<html:html>
<head>
	<link rel="stylesheet" type="text/css"
		href="${contextPath}/css/style.css" />
	<meta name="keywords" content="blast, yrc, module, michael, wilson">
</head>
<body>
	<div class="error">
		<logic:present name="org.apache.struts.action.ERROR">
			<div class="errors">
				<table>
					<tr>
						<td>
							<html:errors />
						</td>
					</tr>
				</table>
			</div>
		</logic:present>

		<logic:messagesPresent message="true">
			<tr>
				<td>
					<%-- title="Errors"  --%>
					<html:messages id="msg" message="true">
						<c:out value="${ msg }" escapeXml="false"
							default="message missing" />
					</html:messages>
					<%-- </span> --%>
				</td>
			</tr>
		</logic:messagesPresent>
	</div>

	<html:form action="/webBlast">



		<div class="container">

			<div class="header">
				Basic Local Alignment Search Tool (BLAST)
			</div>

			<div class="query">

				<div class="query_label">
					&nbsp;Enter Query Sequence
				</div>

				<div class="query_content">
					<span class="field"><span class="req">*</span>Accession
						number(s), gi(s), or FASTA sequence(s)</span>
					<br />
					<html:textarea rows="10" cols="110" property="query"></html:textarea>
					<p>
						"Query sequence(s) to be used for a BLAST search should be pasted
						in the text area. It automatically determines the format or the
						input. To allow this feature there are certain conventions
						required with regard to the input of identifiers."
					</p>
				</div>

			</div>


			<div class="parameter">
				<div class="parameter_label">
					&nbsp;Parameter Selection
				</div>

				<div class="parameter_content">
					<span class="req">All Parameter fields are requred.</span>
					<div class="task">
						<span class="field">Blast Type: &nbsp;</span>
						<html:radio property="task" value="blastn">blastn</html:radio>
						<html:radio property="task" value="blastp">blastp</html:radio>
					</div>
					<div class="outfmt">
						<span class="field">Output Format: &nbsp;</span>
						<html:radio property="outfmt" value="5">XML</html:radio>
						<html:radio property="outfmt" value="6">Tabular</html:radio>
						<html:radio property="outfmt" value="8">ASN.1 (Text)</html:radio>
						<html:radio property="outfmt" value="10">CSV</html:radio>
					</div>
					<div class="alignments">
						<span class="field">Number of Alignments <span
							class="short">(integer >=0)</span>: &nbsp;</span>
						<html:text property="alignments"></html:text>
					</div>
					<div class="descriptions">
						<span class="field">Number of Descriptions <span
							class="short">(integer >=0)</span>: &nbsp;</span>
						<html:text property="descriptions"></html:text>
					</div>
					<div class="database">
						<span class="field">Database: &nbsp;</span>
						<html:select property="database">
							<html:option value="refseq_rna">refseq_rna</html:option>
						</html:select>
					</div>
				</div>
			</div>

			<div class="results">

				<div class="results_label">
					&nbsp;Results
				</div>

				<div class="results_content">
					<p>
						<span class="req">*</span> Provide at least one of the following
						parameters for output. If both are provided, the results will be
						saved to the directory specified in config and emailed to you. If
						no Job Title is provided, the date stamp will be used as the
						filename.
					</p>
					<span class="field">Job Title: <span class="short">(optional
							if 'Email' is provided)</span> &nbsp;</span>
					<html:text property="filename"></html:text>
					<br />
					<br />
					<span class="field">Email: <span class="short">(optional
							if 'Job Title' is provided)</span> &nbsp;&nbsp;</span>
					<html:text property="email"></html:text>
				</div>

			</div>

			<div class="footer">
				<br />
				<div class="submit">
					<html:submit value="BLAST" />
				</div>

			</div>

		</div>

	</html:form>
</body>
</html:html>