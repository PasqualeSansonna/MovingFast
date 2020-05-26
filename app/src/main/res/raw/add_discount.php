<?php
// aggiunge lo sconto dato l'id utente e lo sconto calcolato in base alle ore di noleggio
include 'vars.php';

header ( 'Content-Type: application/json' );

$idutente = $_GET ["id"];
$sconto = $_GET ["sconto"];



if ($idutente != null && $sconto != null ) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "UPDATE utenti SET sconto='$sconto' WHERE id_utente = '$idutente'";

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