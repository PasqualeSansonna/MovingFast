<?php

include 'vars.php';
header ( 'Content-Type: application/json' );

$idUtente= $_GET['idU'];
if ( $idUtente != null )
{
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	if (mysqli_connect_errno ()) 
	{
		$post_error = json_encode ( array (
			'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}
	
	$query = "SELECT MAX(id_noleggio) AS id_noleggio,id_monopattino from noleggi where id_utente = '$idUtente' AND stato = 0";
	$result = mysqli_query ( $conn, $query );
	$number = mysqli_fetch_array ( $result );
	if ( $number != null )
	{
		$idNoleggio= $number['id_noleggio'];
		$idMonopattino = $number['id_monopattino'];
			$post_data = json_encode ( array (
				'idRent' => $idNoleggio,
				'idScooter' => $idMonopattino
			), JSON_FORCE_OBJECT );
			echo $post_data;
			echo "Registrazione del noleggio effettuata con successo";
	}
	else{
		$post_data = json_encode ( array (
				'idRent' => '-1',
				'idScooter' => '-1'
				), JSON_FORCE_OBJECT );
				echo $post_data;
				echo "Non risulta nessun noleggio per questo utente";
	}
}
else
{
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
}
?>