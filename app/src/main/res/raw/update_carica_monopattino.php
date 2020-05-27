<?php
// modifica l'utente dato l'id utente 
include 'vars.php';

header ( 'Content-Type: application/json' );

$idMonopattino = $_GET ["id"];



if ($idMonopattino != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "UPDATE monopattini SET stato_batteria='100' WHERE id_monopattino = '$idMonopattino'";
	
	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}

	mysqli_query ( $conn, $query );
		
	$post_data = json_encode ( array (
	    'ok' => true
    ), JSON_FORCE_OBJECT );
	
	echo $post_data;
	mysqli_close ( $conn );

}else {
	
	$post_error = json_encode ( array (
	'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
	}

?>