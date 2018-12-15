$(document).ready(
    function() {
        $("#shortener").submit(

            function(event) {

                event.preventDefault();


                var data = {}
                var Form = this;

                $.each(this.elements, function(i, v){
                    var input = $(v);
                    data[input.attr("name")] = input.val();
                    delete data["undefined"];
                });

                $.ajax({
                    type : "POST",
                    url : "/link",
                    dataType : "json",
                    data: JSON.stringify(data),
                    context: Form,
                    //data : $(this).serialize(),
                    //data: $.param({originalUrl: $('#originalUrl').val()}),
                    success : function(msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.shortUrl
                            + "'>"
                            + msg.shortUrl
                            + "</a></div>");
                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });