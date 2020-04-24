<?php
// aggiunge un nuovo utente prendendo la password, nome, cognome ed email e
// restituisce un oggetto JSON con l'id dell'utente appena aggiunto
include 'vars.php';

header ( 'Content-Type: application/json' );

$pw = $_GET ["pw"];
$nome = $_GET ["nome"];
$cognome = $_GET ["cognome"];
$email = $_GET ["email"];

if ($pw != null && $nome != null && $cognome != null && $email != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "insert into utenti(email, password, nome, cognome, manutentore) values ('$email', '$pw', '$nome', '$cognome', '0')";
	// $id = "select max(id_utente) as id from utenti";
	$utente = "select id_utente from utenti where email='$email'";
	
	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}
	
	$check_result = mysqli_query ($conn, $utente );
	$check_number = mysqli_fetch_array ( $check_result );
	if ($check_number != null) {
		$post_error = json_encode ( array (
				'err' => 'Utente esistente!' 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	} else {
		mysqli_query ( $conn, $query );
		$result = mysqli_query ( $conn, $utente );
		$number = mysqli_fetch_array ( $result );
		
		$post_data = json_encode ( array (
				'id' => $number ['id_utente'] 
		), JSON_FORCE_OBJECT );
		echo $post_data;
	}
	
	mysqli_close ( $conn );
} else {
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
}

?>