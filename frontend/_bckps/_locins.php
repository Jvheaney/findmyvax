<?php
$ipadd = $_SERVER['REMOTE_ADDR'];
if($ipadd == gethostbyname("") || $ipadd == gethostbyname("")){
include '../localArguments.php';

$loc = $_POST['loc'];
$type = $_POST['type'];
$slots = $_POST['slots'];
$appt_date = $_POST['appt_date'];

if(strlen($loc) >= 1 && strlen($slots) > 0 && $type >= 0 && strlen($appt_date) > 0){
  $prep = pg_prepare($con, 'ins_loc', "INSERT INTO locations_slots (location_code, type, slots_open, appt_date, delta) VALUES ($1, $2, $3, $4, $3) ON CONFLICT ON CONSTRAINT uniq_loc_type_date DO UPDATE SET slots_open=$3, last_updated=now(), delta=$3-locations_slots.slots_open WHERE locations_slots.location_code=$1 AND locations_slots.type=$2 AND locations_slots.appt_date=$4");
  $exec = pg_execute($con, 'ins_loc', array($loc, $type, $slots, $appt_date));

  if (pg_affected_rows($exec) == 1){
    echo "success";
  }
  else{
    echo "query-fail " . "type: " . $type . " slots: " . $slots . " appt_date: " . $appt_date . " loc: " . $loc;
  }

  die();

}
else{
  echo "error";
}


}
else{
  echo "auth-fail";
}
?>
