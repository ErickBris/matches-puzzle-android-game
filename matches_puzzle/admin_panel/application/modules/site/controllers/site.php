<?php

if (!defined("BASEPATH"))
    exit("No direct script access allowed");

class Site extends back {

    public function __construct() {
        parent::__construct();
        $this->lang->load("site", "english");
        $this->load->model("site_model", "site");
    }

    //=====================================================================

    public function get_updates($last_level) {
        $this->db->where("_leid > ", $last_level);
        $this->db->where("le_status", 1);
        $result["levels"] = $this->db->get("levels")->result_array();

        echo json_encode($result);
    }

    //=====================================================================

    public function random_matches() {


        $i = 1;
        while ($i <= 100) {
            $x = rand(0, 9);
            $y = rand(0, 9);
            $r = rand(0, 9);
            if (($x + $y) <= 9) {
                $this->db->set("le_x_value", $x);
                $this->db->set("le_operator", "+");
                $this->db->set("le_y_value", $y);
                $this->db->set("le_r_value", $x + $y);
                $this->db->insert("levels");
                $i++;
            }
        }

        $data["levels"] = $this->db->get("levels")->result_array();
        $this->load->view("front/matches", $data);
    }

    //=====================================================================

    public function show_level($le_id) {
        $this->db->where("_leid", $le_id);
        $data["level"] = $this->db->get("levels")->row();

        $x = $data["level"]->le_x_value;
        $y = $data["level"]->le_y_value;
        $r = $data["level"]->le_r_value;

        $data["x_matches"] = $this->get_num_matches_array($x);
        $data["y_matches"] = $this->get_num_matches_array($y);
        $data["r_matches"] = $this->get_num_matches_array($r);

        $data["market_link"] = $this->db->get("users")->row();
        $this->load->view("front/level", $data);
    }

    //=====================================================================
    
    function get_num_matches_array($num) {
        $numMatches = array();
        switch ($num) {
            case 0:
                $numMatches = array("match_on", "match2_on", "match2_on", "match_off", "match2_on", "match2_on", "match_on");
                break;
            case 1:
                $numMatches = array("match_off", "match2_off", "match2_on", "match_off", "match2_off", "match2_on", "match_off");
                break;
            case 2:
                $numMatches = array("match_on", "match2_off", "match2_on", "match_on", "match2_on", "match2_off", "match_on");
                break;
            case 3:
                $numMatches = array("match_on", "match2_off", "match2_on", "match_on", "match2_off", "match2_on", "match_on");
                break;
            case 4:
                $numMatches = array("match_off", "match2_on", "match2_on", "match_on", "match2_off", "match2_on", "match_off");
                break;
            case 5:
                $numMatches = array("match_on", "match2_on", "match2_off", "match_on", "match2_off", "match2_on", "match_on");
                break;
            case 6:
                $numMatches = array("match_on", "match2_on", "match2_off", "match_on", "match2_on", "match2_on", "match_on");
                break;
            case 7:
                $numMatches = array("match_on", "match2_off", "match2_on", "match_off", "match2_off", "match2_on", "match_off");
                break;
            case 8:
                $numMatches = array("match_on", "match2_on", "match2_on", "match_on", "match2_on", "match2_on", "match_on");
                break;
            case 9:
                $numMatches = array("match_on", "match2_on", "match2_on", "match_on", "match2_off", "match2_on", "match_on");
                break;
            default:
                $numMatches = array();
                break;
        }

        return $numMatches;
    }

}

