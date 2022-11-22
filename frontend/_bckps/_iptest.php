<?php
$ipadd = $_SERVER['REMOTE_ADDR'];
if($ipadd == gethostbyname("") || $ipadd == gethostbyname("")){
include '../localArguments.php';

$pc = $_POST['postal_code'];
$distance = $_POST['distance'];

$pc_prefix = substr($pc, 0, 3);
$distance_query = $distance/110.574; //Converts to degrees latitude (0.01 deg = 1.11 km ish)

$prep = pg_prepare($con, 'get_long_lat', "SELECT longitude, latitude FROM geoip_blocks WHERE postal_code=$1 AND registered_country_geoname_id='6251999' LIMIT 1;");
$exec = pg_execute($con, 'get_long_lat', array($pc_prefix));
$longlat = pg_fetch_row($exec);
$long = $longlat[0];
$lat = $longlat[1];

echo $long . " " . $lat;

/*$query = "SELECT DISTINCT location_code, location_name, (point (longitude,latitude) <-> point (" . $long . ", " . $lat . " )) as distance FROM locations WHERE point (longitude,latitude) <@ circle '((" . $long . "," . $lat . "), " . $distance_query . ")' ORDER BY distance;";
$exec = pg_Exec($query);
$clinics = pg_fetch_all($exec);
echo json_encode($clinics);
*/
//$prep_sites = pg_prepare($con, 'getsites', "SELECT DISTINCT postal_code, (point (longitude,latitude) <-> point ($1,$2)) as distance FROM geoip_blocks WHERE point (longitude,latitude) <@ circle '(($1,$2), $3)' ORDER BY distance;");
//$exec_sites = pg_execute($con, 'getsites', array($long, $lat, $distance_query));
//$clinics = pg_fetch_all($exec_sites);


}
else{
  echo "auth-fail";
}
?>
