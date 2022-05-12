<div>
    <ul class="breadcrumb">
        <li>
            <a href="<?php echo base_url(); ?>admin/dashboard"><?php echo lang("title_dashboard"); ?></a> <span class="divider">/</span>
        </li>
        <li>
            <?php echo lang("title_levels"); ?>
        </li>
    </ul>
</div>
<a class="btn btn-info action-deactivate" href="<?php echo base_url(); ?>levels/admin_levels/create">
    <i class="icon-plus icon-white"></i>
    <?php echo lang("button_new_level"); ?>                                            
</a>
<div class="row-fluid sortable">
    <?php if ($levels) { ?>

        <div class="box span12">

            <div class="box-header well" data-original-title>
                <h2><i class="icon-flag"></i><?php echo lang("title_levels"); ?></h2>            
            </div>

            <div class="box-content">
                <?php echo form_open("levels/admin_levels/operation/", array("id" => "form", "name" => "myform")); ?>
                <div id="per_page_menu">
                    <select name="per_page" onchange="this.form.submit();" class="styledselect_pages">
                        <option value="">Number of rows</option>
                        <option value="10">10</option>
                        <option value="25">25</option>
                        <option value="50">50</option>
                        <option value="100">100</option>
                    </select>
                </div>
                <?php if ($this->session->flashdata("noti_msg")) { ?>                           
                    <div class="alert alert-success">
                        <button type="button" class="close" data-dismiss="alert">×</button>
                        <strong><?php echo $this->session->flashdata("noti_msg"); ?></strong>
                    </div><!-- notification msgsuccess -->                              
                <?php } ?>                

                <table class="table table-striped table-bordered bootstrap-datatable datatable">
                    <thead>
                        <tr>                                               
                            <th><input type="checkbox" id="checkall" /></th>
                            <th><a href="">#</a></th>
                            <th width="15%"><?php echo lang("tbl_title_level_number"); ?></th>
                            <th><?php echo lang("tbl_title_x_value"); ?></th>                                                       
                            <th><?php echo lang("tbl_title_operator"); ?></th>                                                       
                            <th><?php echo lang("tbl_title_y_value"); ?></th>                                                       
                            <th><?php echo lang("tbl_title_r_value"); ?></th>                                                       
                            <th><?php echo lang("tbl_title_moves"); ?></th>                                                                                  
                            <th><?php echo lang("tbl_title_solution"); ?></th>                                                                                  
                            <th><?php echo lang("tbl_title_action"); ?></th>                     
                        </tr>
                    </thead>   
                    <tbody>

                        <?php foreach ($levels as $le) { ?> 
                            <tr class="<?php
                    if (($le["le_number"] % 2) == 0) {
                        echo 'alternate-row';
                    }
                            ?>">
                                <td class="center"><input type="checkbox" name="rows[]" value="<?php echo $le["_leid"]; ?>" class="checkrow" /></td>
                                <td class="center"><?php echo $le["le_number"]; ?></td>
                                <td class="center"><?php echo $le["le_number"]; ?></td>
                                <td class="center"><?php echo $le["le_x_value"]; ?></td>
                                <td class="center"><?php echo $le["le_operator"]; ?></td>
                                <td class="center"><?php echo $le["le_y_value"]; ?></td>
                                <td class="center"><?php echo $le["le_r_value"]; ?></td>
                                <td class="center"><?php echo $le["le_moves"]; ?></td>                                                                                                                           
                                <td class="center"><?php echo $le["le_solution"]; ?></td>                                                                                                                           
                                <td class="center">                                     
                                    <a href="<?php echo base_url(); ?>levels/admin_levels/edit/<?php echo $le["_leid"]; ?>" title="<?php echo lang("btn_edit"); ?>"><img src="<?php echo base_url(); ?>global/views/back/img/icons/edit-icon.png" alt="<?php echo lang("btn_edit"); ?>" /></a> &nbsp;&nbsp;|&nbsp;&nbsp;                
                                    <?php if ($le["le_status"] == 0) { ?>
                                        <a href="<?php echo base_url(); ?>levels/admin_levels/activate/<?php echo $le["_leid"]; ?>" title="<?php echo lang("btn_activate"); ?>"><img src="<?php echo base_url(); ?>global/views/back/img/icons/activate-icon.png" alt="<?php echo lang("btn_activate"); ?>" /></a> &nbsp;&nbsp;|&nbsp;&nbsp;
                                    <?php } else { ?>
                                        <a href="<?php echo base_url(); ?>levels/admin_levels/deactivate/<?php echo $le["_leid"]; ?>" title="<?php echo lang("btn_deactivate"); ?>"><img src="<?php echo base_url(); ?>global/views/back/img/icons/deactivate-icon.png" alt="<?php echo lang("btn_deactivate"); ?>" /></a> &nbsp;&nbsp;|&nbsp;&nbsp;
                                    <?php } ?>
                                    <a href="javascript:void(0)" title="<?php echo lang("btn_delete"); ?>" onclick="return confirm('Do you really want to delete this row(s)?');"><img src="<?php echo base_url(); ?>global/views/back/img/icons/delete-icon.png" alt="<?php echo lang("btn_delete"); ?>" /></a>                                
                                </td>
                            </tr>                                        
                        <?php } ?>                    
                    </tbody>
                </table>

                <div class="pagination pagination-centered"><ul><?php echo $pagination; ?></ul></div>

                <!--  start actions-box ............................................... -->
                <div>                                              
                    <button name="activate" class="btn btn-success action-activate activate oButton">
                        <i class="icon-ok icon-white"></i>  
                        <?php echo lang("btn_activate"); ?>                                            
                    </button>
                    <button name="deactivate" class="btn btn-info action-deactivate deactivate oButton">
                        <i class="icon-minus icon-white"></i>
                        <?php echo lang("btn_deactivate"); ?>                                            
                    </button>
                    <button name="delete" class="btn btn-danger action-delete delete oButton">
                        <i class="icon-trash icon-white"></i> 
                        <?php echo lang("btn_delete"); ?>
                    </button>
                </div>
                <?php echo form_close(); ?> 
                <!-- end actions-box........... -->
            </div>
        </div><!--/span-->
    <?php } else { ?>
        <br clear="all" />            
        <div class="alert alert-info">                    
            <strong><?php echo lang("noti_info_nodata"); ?></strong>
        </div><!-- notification msginfo -->      
    <?php } ?>

</div><!--/row-->




