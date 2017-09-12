$( document ).ready(function() {
    $('#download').click(function(e){
        var  formData = $('#url').val();
        formData=formData.replace('m.youtube','www.youtube');
        $.ajax({
            type:'POST',
            url:'http://localhost:9090',
            traditional: true,
            data: {
               'url':formData
            },
            success:function(result){
                alert("Request sent for download");
            },
            error:function(xhr,err,stat){
                alert("Error in download");
            }
        });
    })
});