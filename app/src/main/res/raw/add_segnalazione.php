<?php
// inserisci la segnalazione dando l'id dell'utente e l'id del monopattino
include 'vars.php';

header ( 'Content-Type: application/json' );

$id_utente = $_GET ["id_utente"];
$id_monopattino = $_GET ["id_monopattino"];
$guasto_freni = $_GET ["guasto_freni"];
$guasto_ruote = $_GET ["guasto_ruote"];
$guasto_manubrio = $_GET ["guasto_manubrio"];
$guasto_acceleratore = $_GET ["guasto_acceleratore"];
$guasto_blocco = $_GET ["guasto_blocco"];
$guasto_altro = $_GET ["guasto_altro"];
$descrizione = $_GET ["descrizione"];


if ($id_utente != null && $id_monopattino != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error ()
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}

	$query = "INSERT INTO segnalazioni (id_utente, id_monopattino, guasto_freni, guasto_ruote,
			  guasto_manubrio, guasto_acceleratore, guasto_blocco, guasto_altro, descrizione)
		      VALUES ('$id_utente', '$id_monopattino', '$guasto_freni', '$guasto_ruote',
		      '$guasto_manubrio', '$guasto_acceleratore', '$guasto_blocco', '$guasto_altro', '$descrizione')";

	$result = mysqli_query ( $conn, $query );
	if ( $result )
	{
		$post_data = json_encode ( array (
	    'ok' => true
		), JSON_FORCE_OBJECT );
	}
	else
	{
		$post_data = json_encode ( array (
	    'ok' => false
		), JSON_FORCE_OBJECT );
	}

	echo $post_data;
	mysqli_close ( $conn );

}else {

	$post_error = json_encode ( array (
	'err' => 'Parametri mancanti!'
	), JSON_FORCE_OBJECT );
	echo $post_error;
	}

?>