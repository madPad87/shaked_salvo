$.getJSON('../api/games', function(data){
    $.each(data, function(i, e){
    $('#myList').append($('<li>').text("Time:" + " " + e.created.toLocaleString() + " " + e.gamePlayers[0].player.username +" "+ "VS" + " " + e.gamePlayers[1].player.username));
    });
});

//function time(milli)
//{
//      var milliseconds = milli % 1000;
//      var seconds = Math.floor((milli / 1000) % 60);
//      var minutes = Math.floor((milli / (60 * 1000)) % 60);
//      return minutes + ":" + seconds + ":" + milliseconds;
//}