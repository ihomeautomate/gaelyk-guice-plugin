<html>
	<body>
		<% ['string', 'leet', 'manuallyInjected', 'customQualifier' ].each { %>
			<div id="${it}">${request[it]}</div>
		<% } %>
	</body>
</html>