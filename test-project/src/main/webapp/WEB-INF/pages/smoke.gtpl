<html>
	<body>
		<% ['string', 'leet', 'manuallyInjected' ].each { %>
			<div id="${it}">${request[it]}</div>
		<% } %>
	</body>
</html>