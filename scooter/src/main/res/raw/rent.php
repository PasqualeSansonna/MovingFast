<?php
include 'vars.php';
header ( 'Content-Type: application/json' );

$idUtente= $_GET['idU'];
$idMonopattino = $_GET['idM'];
$latitudine = $_GET['lat'];
$longitudine = $_GET['long'];
$data = $_GET['data'];
$ora = $_GET['ora'];

if ( $idUtente != null && $idMonopattino != null && $latitudine != null && $longitudine  != null && $data != null  && $ora != null )
{
	// Check connection
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	if (mysqli_connect_errno ()) 
	{
		$post_error = json_encode ( array (
			'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}
	$query = "INSERT INTO noleggi (id_utente, id_monopattino, ora_inizio, latitudine_partenza, longitudine_partenza, data) 
			VALUES ('$idUtente','$idMonopattino','$ora','$latitudine','$longitudine','$data')";
	$result = mysqli_query ( $conn, $query );
	if ( $result )
	{
		$query = "SELECT * FROM noleggi where id_utente = '$idUtente' AND id_monopattino = '$idMonopattino' AND ora_inizio = '$ora' AND latitudine_partenza = '$latitudine'
					AND longitudine_partenza = '$longitudine' AND data = '$data'";
		$result = mysqli_query ( $conn, $query );
		$number = mysqli_fetch_array ( $result );
		if ( $number != null )
		{
			$idNoleggio= $number['id_noleggio'];
			$post_data = json_encode ( array (
				'idRent' => $idNoleggio
			), JSON_FORCE_OBJECT );
			echo $post_data;
			echo "Registrazione del noleggio effettuata con successo";

		}
		else
		{
			$post_data = json_encode ( array (
				'idRent' => '-1'
				), JSON_FORCE_OBJECT );
				echo $post_data;
				echo "Query inserimento noleggio nel db ok ma errore query per ottenere id del noleggio";
		}
	}
	else
	{
		$post_data = json_encode ( array (
				'idRent' => '-1'
				), JSON_FORCE_OBJECT );
		echo "Errore query di inserimento noleggio nel database";
	
	}
	mysqli_close ( $conn );
}
else
{
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
}
?>