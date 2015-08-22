$('.panel').hide();
window.rootURL = "";
window.userName = "";
window.password = "";
$('#setUserButton').click(function() {

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
    showalert("User set to " + window.userName, "alert-success");
});


$('#list-button').click(function() {
    if(window.userName ==="") {
        showalert("Set user name first", "alert-danger");
        return;
    }
    if(window.password ==="") {
            showalert("Set password first", "alert-danger");
            return;
    }
    //$('#listStatus').hide();
    showalert( "Getting list of files for " + window.userName, "alert-info");
    var dataLength;
    $.get(window.rootURL + "users/" + window.userName + "/" + window.password, function(data, status) {

        $('.panel').show();
        $('.table tbody').remove();
        $('.table thead').remove();
        $('.table').append('<thead><tr><th>File Name</th><th>Size (bytes)</th></tr></thead><tbody>');
        dataLength = data.length;
        for(i = 0; i < data.length; i++) {
            $('.table').append('<tr><td><a href="users/' + window.userName+ "/" + window.password +'/files/' + data[i].fileName + '">' + data[i].fileName + '</a></td><td>' + data[i].size+ '</td></tr>');
        }
        $('.table').append('</tbody>');
        if(dataLength === 0) {
            showalert( "No files for user " + window.userName, "alert-warning");
        } else {
            showalert( "Received file list for " + window.userName, "alert-success");
        }
    });

});

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

    $.ajax({
      type: "POST",
      url: window.rootURL + "users/" + window.userName+ "/" + window.password,

      success: function(){
          showalert("Successfully added user " + window.userName, "alert-success");
      },
      error: function(){
                showalert("Failed to add user " + window.userName, "alert-danger");
            },
    });

});

$('#upload-button').click(function(){
    if(window.userName ==="") {
        showalert("Set user name first", "alert-danger");
        return;
    }
    if(window.password ==="") {
                showalert("Set password first", "alert-danger");
                return;
    }

    var fileName = $('#file').val().split('\\').pop();
    var data = new FormData($('#fileForm')[0]);
    //data.append("file",$('#file').files );

    showalert("Uploading file " + fileName, "alert-info");
    $.ajax({
      type: "POST",
      url: window.rootURL + "users/" + window.userName + "/" + window.password + "/files/" + fileName,
      data: data,
      contentType: false,
      processData: false,

      success: function(){
          showalert("Successfully added file " + fileName, "alert-success");
      },

      error: function(){
                      showalert("Failed to add file " + fileName, "alert-danger");
                  }

    });

});

var showalert = function(message,alerttype) {
    $('#alert_placeholder div').remove();
    $('#alert_placeholder').append('<div id="alertdiv" class="alert ' +  alerttype + '">'+message+'</div>')

    setTimeout(function() { // this will automatically close the alert and remove this if the users doesnt close it in 5 secs


      $("#alertdiv").remove();

    }, 10000);
  }



