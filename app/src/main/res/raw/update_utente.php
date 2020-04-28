<?php
// modifica l'utente dato l'id utente 
include 'vars.php';

header ( 'Content-Type: application/json' );

$idutente = $_GET ["id"];
$nome = $_GET ["nome"];
$cognome = $_GET ["cognome"];
$email = $_GET ["email"];



if ($nome != null && $cognome != null && $email != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "UPDATE utenti SET email='$email', nome='$nome', cognome='$cognome' WHERE id_utente = '$idutente'";
	$controllo = "SELECT id_utente FROM utenti WHERE email = '$email'";
	
	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}
	

	
	$check_result = mysqli_query ($conn, $controllo );
	$check_number = mysqli_fetch_array ( $check_result );
	if ($check_number != null) {
		$post_error = json_encode ( array (
				'err' => 'Utente esistente!' 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	} else {

		mysqli_query ( $conn, $query );
		
		$post_data = json_encode ( array (
			'ok' => true 
		), JSON_FORCE_OBJECT );
	
		echo $post_data;
		
	} 
	mysqli_close ( $conn );

}else {
	
	$post_error = json_encode ( array (
	'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
	}

?>