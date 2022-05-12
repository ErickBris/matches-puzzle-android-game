<?php

if (!defined("BASEPATH"))
    exit("No direct script access allowed");

class Levels_model extends CI_Model {

    private $table = "levels";

    public function __construct() {
        parent::__construct();
    }

    //=====================================================================

    public function overview($limit, $start) {

        $start = ($start != 0) ? (($limit * $start) - $limit) : 0;
        $this->db->limit($limit, $start);
        $this->db->order_by("le_number", "asc");
        return $this->db->get($this->table)->result_array();
    }

    //=====================================================================       

    public function get_levels_count() {

        return $this->db->count_all($this->table);
    }

    //=====================================================================

    public function create() {

        $this->db->set("le_number", $this->input->post("le_number"));
        $this->db->set("le_x_value", $this->input->post("le_x_value"));
        $this->db->set("le_y_value", $this->input->post("le_y_value"));
        $this->db->set("le_r_value", $this->input->post("le_r_value"));
        $this->db->set("le_operator", $this->input->post("le_operator"));
        $this->db->set("le_moves", $this->input->post("le_moves"));
        $this->db->set("le_solution", $this->input->post("le_solution"));
        $this->db->set("le_open", 0);
        $this->db->set("le_completed", 0);        
        $this->db->set("le_status", $this->input->post("le_status"));
        $this->db->insert($this->table);

        return TRUE;
    }
    
    //=====================================================================

    public function is_equation_exist() {
      
        $this->db->where("le_x_value", $this->input->post("le_x_value"));
        $this->db->where("le_y_value", $this->input->post("le_y_value"));
        $this->db->where("le_r_value", $this->input->post("le_r_value"));
        $this->db->where("le_operator", $this->input->post("le_operator"));
        $this->db->where("le_moves", $this->input->post("le_moves"));       
        $result = $this->db->get($this->table)->row();

        if ($result) {
            return $result;
        } else {
            return FALSE;
        }
    }
    
    //=====================================================================

    public function get_max_level() {
        $this->db->select_max("le_number");
        $this->db->select("_leid");        
        $max = $this->db->get($this->table)->row();
        
        return $max->le_number;
    }
    
 
    //=====================================================================

    public function update_inserted_row_order($inserted_id) {
        $this->db->select_max("le_number");
        $max_order = $this->db->get($this->table)->row();

        $this->db->where("_leid", $inserted_id);
        $this->db->set("le_number", $max_order->le_number + 1);
        $this->db->update($this->table);
    }

    //=====================================================================

    public function edit($_leid) {

        $this->db->where("_leid", $_leid);
        $this->db->set("le_number", $this->input->post("le_number"));
        $this->db->set("le_x_value", $this->input->post("le_x_value"));
        $this->db->set("le_y_value", $this->input->post("le_y_value"));
        $this->db->set("le_r_value", $this->input->post("le_r_value"));
        $this->db->set("le_operator", $this->input->post("le_operator"));
        $this->db->set("le_moves", $this->input->post("le_moves"));           
        $this->db->set("le_solution", $this->input->post("le_solution"));           
        $this->db->set("le_status", $this->input->post("le_status"));
        $this->db->update($this->table);

        return TRUE;
    }

    //=====================================================================

    public function get_this_level($_leid) {

        $this->db->where("_leid", $_leid);
        return $this->db->get($this->table)->row();
    }

    //=====================================================================

    public function order_up($_leid) {

        $this->db->where("_leid", $_leid);
        $this->db->select("le_number");
        $order = $this->db->get($this->table)->row();

        $this->db->select_max("le_number");
        $this->db->select("_leid");
        $this->db->where("le_number < ", $order->le_number);
        $max = $this->db->get($this->table)->row();
        if ($max->le_number != 0) {
            $this->db->where("le_number", $max->le_number);
            $this->db->set("le_number", $order->le_number);
            $this->db->update($this->table);

            $this->db->where("_leid", $_leid);
            $this->db->set("le_number", $max->le_number);
            $this->db->update($this->table);
        }

        return TRUE;
    }

    //=====================================================================

    public function order_down($_leid) {

        $this->db->where("_leid", $_leid);
        $this->db->select("le_number");
        $order = $this->db->get($this->table)->row();

        $this->db->select_min("le_number");
        $this->db->select("_leid");
        $this->db->where("le_number > ", $order->le_number);
        $max = $this->db->get($this->table)->row();
        if ($max->le_number != 0) {
            $this->db->where("le_number", $max->le_number);
            $this->db->set("le_number", $order->le_number);
            $this->db->update($this->table);

            $this->db->where("_leid", $_leid);
            $this->db->set("le_number", $max->le_number);
            $this->db->update($this->table);
        }

        return TRUE;
    }

    //=====================================================================

    public function operation() {
        if (isset($_POST["rows"])) {
            foreach ($_POST["rows"] as $_leid) {
                if (isset($_POST["activate"])) {
                    $this->activate($_leid);
                } elseif (isset($_POST["deactivate"])) {
                    $this->deactivate($_leid);
                } elseif (isset($_POST["delete"])) {
                    /*$this->db->where("_leid", $_leid);
                    $this->db->select("le_number");
                    $result = $this->db->get($this->table)->row();                   
                    
                    $this->delete($_leid, $result->le_number);*/
                }
            }
        }
    }

    //=====================================================================

    public function activate($_leid) {

        $this->db->where("_leid", $_leid);
        $this->db->set("le_status", "1");
        $this->db->update($this->table);

        return TRUE;
    }

    //=====================================================================

    public function deactivate($_leid) {

        $this->db->where("_leid", $_leid);
        $this->db->set("le_status", "0");
        $this->db->update($this->table);

        return TRUE;
    }

    //=====================================================================

    public function delete($_leid, $le_number) {

        $this->db->where("_leid", $_leid);
        if ($this->db->delete($this->table)) {
            $query = "UPDATE {$this->table} ";
            $query.= "SET le_number = le_number - 1 ";
            $query.= "WHERE le_number > '{$le_number}'";
            $this->db->query($query);                        
        }

        return TRUE;
    }   

}