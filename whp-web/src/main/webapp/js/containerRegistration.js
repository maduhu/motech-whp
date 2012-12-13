$().ready(function() {
    $.metadata.setType("attr", "validate");

	$("#container-registration-form").validate({
		rules: {
			containerId: {
			                required: true,
                         	digits: true
                         },
            providerId: {
                    required: true
            }
		},
		messages: {
			containerId: {
				required: "Please enter the container id",
			},
		    providerId: {
		        required: "Please enter the provider id"
		    },
			instance: {
				required: "Please select the instance"
			}
		}
	});

    createAutoClosingAlert(".container-registration-message-alert", 5000);
    $('input:text:first').focus();
});
