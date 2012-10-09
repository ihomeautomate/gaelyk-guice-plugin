<html>
	<body>
		<% ['string', 'integer', 'manuallyInjected' ].each { %>
			<div id="${it}">${request[it]}</div>
		<% } %>
	</body>
</html>