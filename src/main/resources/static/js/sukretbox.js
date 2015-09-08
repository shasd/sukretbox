$('.panel').hide();
window.rootURL = "";
window.userName = "";

var listFiles = function(showSuccess) {
    //$('#listStatus').hide();
    showalert( "Getting list of files for " + window.userName, "alert-info");
    var dataLength;
    $.get("list", function(data, status) {

        $('.panel').show();
        $('.table tbody').remove();
        $('.table thead').remove();
        $('.table').append('<thead><tr><th>File Name</th><th>Size (bytes)</th></tr></thead><tbody>');
        dataLength = data.length;
        for(i = 0; i < data.length; i++) {
            $('.table').append('<tr><td><a href="files/' + data[i].fileName + '">' + data[i].fileName + '</a></td><td>' + data[i].size+ '</td></tr>');
        }
        $('.table').append('</tbody>');
        if(dataLength === 0) {
            showalert( "No files for user " + window.userName, "alert-warning");
        } else {
            if(showSuccess)
                showalert( "Received file list for " + window.userName, "alert-success");
        }
    });

};

$('#newUserButton').click(function(){
    if($('#userName').val() === "") {
            showalert("Enter a user name ", "alert-danger");
            return;
        }
        if($('#password').val() === "") {
            showalert("Enter a password ", "alert-danger");
            return;
        }
    window.userName = $('#userName').val();
    window.password = $('#password').val();
    showalert("Registering user " + window.userName, "alert-info");
    $.ajax({
      type: "PUT",
      url: window.rootURL + "register/" + window.userName+ "/" + window.password,

      success: function(){
          showalert("Successfully added user " + window.userName, "alert-success");
      },
      error: function(){
                showalert("Failed to add user " + window.userName, "alert-danger");
            },
    });

});

$('#upload-button').click(function(){
    var fileName = $('#file').val().split('\\').pop();
    var data = new FormData($('#fileForm')[0]);
    //data.append("file",$('#file').files );

    showalert("Uploading file " + fileName, "alert-info");
    $.ajax({
      type: "POST",
      url: "files/" + fileName,
      data: data,
      contentType: false,
      processData: false,

      success: function(){
          listFiles(false);
          showalert("Successfully added file " + fileName, "alert-success");

      },

      error: function(){
                      showalert("Failed to add file " + fileName, "alert-danger");
                  }

    });

});

var showalert = function(message, alertType) {
    $('#alert_placeholder div').remove();
    $('#alert_placeholder').append('<div id="alertdiv" class="alert ' +  alertType + '">'+message+'</div>')

    setTimeout(function() { // this will automatically close the alert and remove this if the users doesnt close it in 5 secs
        $("#alertdiv").remove();
    }, 10000);
  }

$(document).ready(function(){
    if($('title').text() === "SukretBox") {
            $.get("username",function(data, status){
            $('#signedInName').text("Signed in as " + data);
            window.userName = data;
            listFiles(true);
        });
    }
});


