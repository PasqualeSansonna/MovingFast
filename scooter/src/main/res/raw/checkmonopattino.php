<?php
include 'vars.php';
header ( 'Content-Type: application/json' );

$id = $_GET["id"];
$lat=$_GET["lat"];
$long=$_GET["long"];
$batteria=$_GET["bat"];

$conn = mysqli_connect ( $host, $user, $password, $db_name );
// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}

$query = "SELECT * FROM monopattini where id_monopattino='$id'"; 
$result = mysqli_query ( $conn, $query );
$row = mysqli_fetch_array($result);
if ( $row != null )
{
	//Se il monopattino esiste già nel db aggiorno le sue informazioni con quelle attuali
	$query = "UPDATE monopattini SET latitudine='$lat', longitudine='$long', stato_batteria = '$batteria' WHERE id_monopattino = '$id'";
	$result = mysqli_query ( $conn, $query );
	if ( $result )
		$post_data = json_encode ( array (
			'id' => $id
	), JSON_FORCE_OBJECT );
	else
		$post_data = json_encode ( array (
			'id' => "null"
	), JSON_FORCE_OBJECT );
}
else
{
	//Significa che non esiste un monopattino con questo id, quindi lo aggiungo al db
	$query = "INSERT INTO monopattini (id_monopattino,latitudine,longitudine,stato_batteria) VALUES ('$id','$lat','$long','$batteria')";
	$result = mysqli_query ( $conn, $query );
	if ( $result )
		$post_data = json_encode ( array (
			'id' => $id
	), JSON_FORCE_OBJECT );
	else
		$post_data = json_encode ( array (
			'id' => "null"
	), JSON_FORCE_OBJECT );
}
	echo $post_data;
	mysqli_close ( $conn );
?>