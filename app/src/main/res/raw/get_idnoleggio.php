<?php
// restituisce l'id noleggio fornendo l'id utente
include 'vars.php';

header ( 'Content-Type: application/json' );

$idutente = $_GET ["id"];

if ($idutente != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "select id_noleggio, id_monopattino from noleggi where id_utente='$idutente' and ora_fine IS NULL";
	
	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}
	
	$result = mysqli_query ( $conn, $query );
	$number = mysqli_fetch_array ( $result );

	$post_data = json_encode ( array (
			'id' => $number ['id_noleggio'],
			'id_scooter' => $number ['id_monopattino'] 
	), JSON_FORCE_OBJECT );
	
	echo $post_data;
	mysqli_close ( $conn );
} else {
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
}

?>