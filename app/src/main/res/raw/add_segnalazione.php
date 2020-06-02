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

if ($id_utente != null && $id_monopattino != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );

	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error ()
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}

	$controlloUtente = "SELECT * FROM segnalazioni WHERE id_monopattino = $id_monopattino";
	$resultControlloUtente = mysqli_query ( $conn, $controlloUtente );
	$count = $resultControlloUtente->num_rows;

	if($count == 0){


	$queryInsert = "INSERT INTO segnalazioni (id_utente, id_monopattino, guasto_freni, guasto_ruote,
			  guasto_manubrio, guasto_acceleratore, guasto_blocco, guasto_altro)
		      VALUES ('$id_utente', '$id_monopattino', '$guasto_freni', '$guasto_ruote',
		      '$guasto_manubrio', '$guasto_acceleratore', '$guasto_blocco', '$guasto_altro')";

	$result = mysqli_query ( $conn, $queryInsert );

	if ( $result ){
		$post_data = json_encode ( array (
	    'ok' => true
		), JSON_FORCE_OBJECT );
	}else{
		$post_data = json_encode ( array (
	    'ok' => false
		), JSON_FORCE_OBJECT );
	}

	echo $post_data;
	mysqli_close ( $conn );

	}else {

	if($guasto_freni == 1){
 		$queryUpdateFreni = "UPDATE segnalazioni SET guasto_freni= $guasto_freni WHERE id_monopattino = $id_monopattino";
 		$resultUpdateFreni = mysqli_query ( $conn, $queryUpdateFreni );
	}
	if($guasto_ruote == 1){
 		$queryUpdateRuote = "UPDATE segnalazioni SET guasto_ruote = $guasto_ruote WHERE id_monopattino = $id_monopattino";
 		$resultUpdateRuote = mysqli_query ( $conn, $queryUpdateRuote );
	}
	if($guasto_manubrio == 1){
 		$queryUpdateManubrio = "UPDATE segnalazioni SET guasto_manubrio = $guasto_manubrio WHERE id_monopattino = $id_monopattino";
 		$resultUpdateManubrio = mysqli_query ( $conn, $queryUpdateManubrio );
	}
	if($guasto_acceleratore == 1){
 		$queryUpdateAcceleratore = "UPDATE segnalazioni SET guasto_acceleratore = $guasto_acceleratore WHERE id_monopattino = $id_monopattino";
 		$resultUpdateAcceleratore = mysqli_query ( $conn, $queryUpdateAcceleratore );
	}
	if($guasto_blocco == 1){
 		$queryUpdateBlocco = "UPDATE segnalazioni SET guasto_blocco = $guasto_blocco WHERE id_monopattino = $id_monopattino";
 		$resultUpdateBlocco = mysqli_query ( $conn, $queryUpdateBlocco );
	}
	if($guasto_altro == 1){
 		$queryUpdateAltro = "UPDATE segnalazioni SET guasto_altro = $guasto_altro WHERE id_monopattino = $id_monopattino";
 		$resultUpdateAltro = mysqli_query ( $conn, $queryUpdateAltro );
	}

	$post_data = json_encode ( array (
	    'ok' => true
		), JSON_FORCE_OBJECT );

	echo $post_data;
	mysqli_close ( $conn );

	}

}else {

	$post_error = json_encode ( array (
	'err' => 'Parametri mancanti!'
	), JSON_FORCE_OBJECT );
	echo $post_error;
	}

?>