<%@ page import="uk.co.metadataconsulting.sentinel.RecordCorrectnessDropdown" %>
<html>
<head>
    <title>Records</title>
    <meta name="layout" content="main" />
</head>
<body>
<nav class="navbar navbar-light bg-light">
    <g:render template="/templates/navbarBrand"/>
    <g:form controller="record" action="index" method="GET"  class="form-inline">
        <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
        <g:select name="correctness" from="${RecordCorrectnessDropdown.values()}" value="${correctness}"/>
        <input type="submit" class="btn-primary btn" value="${g.message(code: 'record.filter', default: 'Filter')}"/>
    </g:form>
    <g:form controller="recordCollection" action="validate" method="POST"  class="form-inline">
        <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
        <input type="submit" class="btn-primary btn" value="${g.message(code: 'record.validate', default: 'Validate')}"/>
    </g:form>
</nav>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index"><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item active" aria-current="page"><g:message code="nav.recordCollection" args="${[recordCollectionId]}" default="Record Collection {0}"/></li>
    </ol>
</nav>

<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>


<g:if test="${recordList}">
    <table class="table table-striped">
        <thead class="thead-dark">
        <tr>
            <th><g:message code="record.th." default="Record"/></th>
        </tr>
        </thead>
        <tbody>
            <g:each var="record" in="${recordList}" status="i">
                <g:if test="${record.valid}">
                    <tr>
                </g:if>
                <g:else>
                    <tr class="alert-danger">
                </g:else>
                <td>
                    <g:link controller="record" action="show" params="[recordCollectionId: recordCollectionId, recordId: record.id]">
                        <g:message code="record.row" args="${(paginationQuery?.offset ?: 0) + i + 1}" default="Row {0}"/>
                    </g:link>
                </td>
                </tr>
            </g:each>
        </tbody>
    </table>
    <g:if test="${recordTotal > paginationQuery?.max}">
        <div class="pagination">
        <g:paginate controller="record"
                    action="index"
                    total="${recordTotal}"
                    max="${paginationQuery?.max}"
                    offset="${paginationQuery?.offset}"
                    params="[correctness: correctness, recordCollectionId: recordCollectionId]" />
        </div>
    </g:if>
</g:if>
<g:render template="/record/paginationinfo"/>
</body>
</html>