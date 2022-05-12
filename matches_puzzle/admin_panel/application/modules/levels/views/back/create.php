<div>
    <ul class="breadcrumb">
        <li>
            <a href="<?php echo base_url(); ?>admin/dashboard"><?php echo lang("title_dashboard"); ?></a> <span class="divider">/</span>
        </li>
        <li>
            <a href="<?php echo base_url(); ?>levels/admin_levels"><?php echo lang("title_levels"); ?></a> <span class="divider">/</span>
        </li>
        <li>
            <?php echo lang("title_new_level"); ?>
        </li>
    </ul>
</div>

<div class="row-fluid sortable">
    <div class="box span12">
        <div class="box-header well" data-original-title>
            <h2><i class="icon-plus"></i> <?php echo lang("title_new_level"); ?></h2>            
        </div>
        <div class="box-content">

            <?php if ($is_equation_exist) { ?>                           
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <strong><?php echo lang("already_exists_msg") . $is_equation_exist->le_number; ?></strong>
                </div><!-- notification msgsuccess -->                              
            <?php } ?>
            <?php //echo form_open_multipart("levels/admin_levels/create", array('class' => 'form-horizontal')); ?>
            <form class="form-horizontal">
                <fieldset>
                    <legend>Add New Level</legend>
                    <div class="control-group">
                        <label class="control-label" for="le_number"><?php echo lang("input_number"); ?></label>
                        <div class="controls">                                                        
                            <input type="text" id="le_number" name="le_number" value="<?php echo set_value("le_number", $max_le_number + 1); ?>" class="input-xlarge focused" readonly="readonly" />                                          
                            <span class="help-inline error"><?php echo form_error("le_number"); ?></span>                    
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label" for="le_x_value"><?php echo lang("input_x_value"); ?></label>
                        <div class="controls">                                                        
                            <input type="text" id="le_x_value" name="le_x_value" value="<?php echo set_value("le_x_value"); ?>" class="input-xlarge focused" />                                          
                            <span class="help-inline error"><?php echo form_error("le_x_value"); ?></span>                    
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label" for="le_operator"><?php echo lang("input_operator"); ?></label>
                        <div class="controls">                            
                            <select id="le_operator" name="le_operator">
                                <option value="">- Choose Operator</option>                                                        
                                <option value="+" <?php echo set_select('le_operator', "+"); ?>>+</option>                                                                             
                                <option value="-" <?php echo set_select('le_operator', "-"); ?>>-</option>                                                                             
                            </select>
                            <span class="help-inline error"><?php echo form_error("le_operator"); ?></span>     
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label" for="le_y_value"><?php echo lang("input_y_value"); ?></label>
                        <div class="controls">                                                        
                            <input type="text" id="le_y_value" name="le_y_value" value="<?php echo set_value("le_y_value"); ?>" class="input-xlarge focused" />                                          
                            <span class="help-inline error"><?php echo form_error("le_y_value"); ?></span>                    
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label" for="le_r_value"><?php echo lang("input_r_value"); ?></label>
                        <div class="controls">                                                        
                            <input type="text" id="le_r_value" name="le_r_value" value="<?php echo set_value("le_r_value"); ?>" class="input-xlarge focused" />                                          
                            <span class="help-inline error"><?php echo form_error("le_r_value"); ?></span>                    
                        </div>
                    </div>                    

                    <div class="control-group">
                        <label class="control-label" for="le_moves"><?php echo lang("input_moves"); ?></label>
                        <div class="controls">                            
                            <select id="le_moves" name="le_moves">
                                <option value="">- Choose Moves</option>                                                        
                                <option value="1" <?php echo set_select('le_moves', "1"); ?>>1</option>                                                                             
                                <option value="2" <?php echo set_select('le_moves', "2"); ?>>2</option>                                                                             
                            </select>
                            <span class="help-inline error"><?php echo form_error("le_moves"); ?></span>     
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label" for="le_solution"><?php echo lang("input_solution"); ?></label>
                        <div class="controls">                                                        
                            <input type="text" id="le_solution" name="le_solution" value="<?php echo set_value("le_solution"); ?>" class="input-xlarge focused" />                                          
                            <span class="help-inline error"><?php echo form_error("le_solution"); ?></span>                    
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label" for="le_status"><?php echo lang("input_status"); ?></label>
                        <div class="controls">
                            <label class="checkbox">
                                <input type="checkbox" id="le_status" value="1" name="le_status" <?php echo set_checkbox("le_status", "1"); ?> />
                                <?php echo lang("input_active"); ?>
                                <span class="help-inline error"><?php echo form_error("le_status"); ?></span> 
                            </label>
                        </div>
                    </div>
                    <div class="form-actions">
                        <input type="submit" name="submit" value="<?php echo lang("input_btn_save"); ?>" class="btn btn-primary" />
                        <input type="reset" name="reset" value="<?php echo lang("input_btn_reset"); ?>" class="btn reset" />
                    </div>
                </fieldset>
                <?php echo form_close(); ?>   

        </div>
    </div><!--/span-->

</div><!--/row-->