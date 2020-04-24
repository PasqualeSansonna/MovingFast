<?php
// verifica la registrazione di un utente avendo email e password
// restituisce un oggetto JSON con id e nome dell'utente se esiste, altrimenti i valori sono null
include 'vars.php';

header ( 'Content-Type: application/json' );

$pw = $_GET ["pw"];
$email = $_GET ["email"];

if ($pw != null && $email != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "select id_utente, nome from utenti where email='$email' and password='$pw'";
	
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
			'id' => $number ['id_utente'],
			'nome' => $number ['nome'] 
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