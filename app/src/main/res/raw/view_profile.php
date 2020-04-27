<?php
//restituisce un oggetto JSON con nome, cognome, email
include 'vars.php';

header ('Content-Type: application/json' );

$id = $_GET ["id_utente"];

if ($id != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "select nome, cognome, email from utenti where id='$id_utente'";

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
			'nome' => $number ['nome'],
			'cognome' => $number ['cognome'],
			'email' => $number ['email'],
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

