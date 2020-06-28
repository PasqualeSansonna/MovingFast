<?php
//restituisce un oggetto JSON  data, durata, importo, ora inizio, ora fine, latitudine partenza, longitudine partenza, latitudine arrivo, longitudine arrivo
include 'vars.php';

header ('Content-Type: application/json' );

$id = $_GET ["id"];

if ($id != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "select data, importo, durata_totale, ora_inizio, ora_fine, latitudine_partenza, longitudine_partenza, latitudine_arrivo,  longitudine_arrivo
	from noleggi
	where id_utente='$id' AND stato > 0";

	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}

	$data_array = array ();
	$durata_totale_array = array ();
	$importo_array = array ();
	$ora_inizio_array = array ();
        $ora_fine_array = array ();
        $latitudine_partenza_array = array ();
        $longitudine_partenza_array = array ();
        $latitudine_arrivo_array = array ();
        $longitudine_arrivo_array = array ();

        
	$result = mysqli_query ( $conn, $query );
		
        while ( $number = mysqli_fetch_array ( $result ) ) {
			$data = $number ['data'];
			$importo = $number ['importo'];
            $durata_totale = $number ['durata_totale'];
			$ora_inizio = $number ['ora_inizio'];
			$ora_fine = $number ['ora_fine'];
			$latitudine_partenza = $number ['latitudine_partenza' ];
			$longitudine_partenza = $number ['longitudine_partenza' ];
			$latitudine_arrivo = $number ['latitudine_arrivo' ];
			$longitudine_arrivo = $number ['longitudine_arrivo' ];
			$data_array[] = $data;
			$durata_totale_array[] = $durata_totale;
			$importo_array[] = $importo;
			$ora_inizio_array[] = $ora_inizio;
            $ora_fine_array[] = $ora_fine;
        	$latitudine_partenza_array[] = $latitudine_partenza;
       		$longitudine_partenza_array[] = $longitudine_partenza;
       		$latitudine_arrivo_array[] = $latitudine_arrivo;
       		$longitudine_arrivo_array[] = $longitudine_arrivo;
	}
	
        $post_data = json_encode ( array (
			'data' => $data_array,
			'importo' => $importo_array,
			'durata_totale' => $durata_totale_array,
			'ora_inizio' => $ora_inizio_array,
			'ora_fine' => $ora_fine_array,
			'latitudine_partenza' => $latitudine_partenza_array,
			'longitudine_partenza' => $longitudine_partenza_array,
			'latitudine_arrivo' => $latitudine_arrivo_array,
			'longitudine_arrivo' => $longitudine_arrivo_array
        ), JSON_FORCE_OBJECT );
	echo $post_data;
} else {
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;	
}

?>
