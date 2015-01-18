#include <pebble.h>
#include <stdio.h> 
#include <math.h>
  
#define TAP_NOT_DATA false
#define KEY_X 0
#define KEY_Y 1
#define KEY_Z 2
#define WHICH_HAND_KEY 4

static BitmapLayer *s_bitmap_layer;
static GBitmap *s_bitmap_drum;
static int left_hand = -1;

static Window *s_main_window;
static TextLayer *s_output_layer;
int sum_x = 0;
int sum_y = 0;
int sum_z = 0;
int it_bg = 0;
int vx = 0;
int vy = 0;
int vz = 0;
int nums = 0;
static int avg_bg_x, avg_bg_y, avg_bg_z;
int send_x = 0;
int send_y = 0;
int send_z = 0;
int count = 0;

  
static void data_handler(AccelData *data, uint32_t num_samples) {
  // Long lived buffer
  static char s_buffer[128];
  // Compose string of all data
//   snprintf(s_buffer, sizeof(s_buffer), 
//     "N X,Y,Z\n0 %d,%d,%d\n1 %d,%d,%d\n2 %d,%d,%d", 
//     data[0].x, data[0].y, data[0].z, 
//     data[1].x, data[1].y, data[1].z, 
//     data[2].x, data[2].y, data[2].z
//   );
  
  /*
  APP_LOG(APP_LOG_LEVEL_DEBUG, "N X,Y,Z\n0 %d,%d,%d\n1 %d,%d,%d\n2 %d,%d,%d\n3 %d,%d,%d\n4 %d,%d,%d",
    data[0].x, data[0].y, data[0].z, 
    data[1].x, data[1].y, data[1].z, 
    data[2].x, data[2].y, data[2].z,
    data[3].x, data[3].y, data[3].z,
    data[4].x, data[4].y, data[4].z);
  */
  
  // discard the next dataset after a confirmed hit
  if (count == 1){
    APP_LOG(APP_LOG_LEVEL_DEBUG, "next data discarded **");
    count = 0;
  }
  else{
    // calculate bg every 10 samples if not peak
  int i = 0;
  it_bg = 0;
  sum_x = 0;
  sum_y = 0;
  sum_z = 0;
  
  for (i=0; i<nums; i++){
    int x = data[i].x;
    int y = data[i].y;
    int z = data[i].z;
    
    int sumq = x*x + y*y + z*z;
    if (sumq > 400000){
      sum_x += x;
      sum_y += y;
      sum_z += z;
      it_bg ++;
    }    
  }
  
   avg_bg_x = sum_x/it_bg;
   avg_bg_y = sum_y/it_bg;
   avg_bg_z = sum_z/it_bg;

  // substract bg, calculate area  
  vx = 0;
  vy = 0;
  vz = 0;
  for (i=0; i<nums; i++){
    int x = data[i].x;
    int y = data[i].y;
    int z = data[i].z;
    vx = vx + x - avg_bg_x;
    vy = vy + y - avg_bg_y;
    vz = vz + z - avg_bg_z;
  }
  
  if (abs(vz)>300 ){
//     APP_LOG(APP_LOG_LEVEL_DEBUG, "**avg bg num %d; X:%d,Y:%d,Z:%d",it_bg,avg_bg_x,avg_bg_y,avg_bg_z);
//     APP_LOG(APP_LOG_LEVEL_DEBUG, "**velocity VX:%d,VY:%d,VZ:%d\n\n",vx,vy,vz);
    
    // apply threshold to give direction
    if (left_hand == -1){
//       APP_LOG(APP_LOG_LEVEL_DEBUG,"RIGHT HAND PLAYER :->");
      // righthand side
      // front
      if ((vx > 300 && (vx-vy)>200) || (vx>600)){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "front!");
        send_x = 1;
  //       send_y = 0;
        send_z = vz;
      }  
      // back
      if ( vx < -100 && (vx-vy)<-100 ){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "back!");
        send_x = -1;
  //       send_y = 0;
        send_z = vz;
      }
      // left
      if (vy>150 && (vy-vx)>100){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "left!");
  //       send_x = 0;
        send_y = 1;
        send_z = vz;
      }
        
      // right
      if (vy<-300 && (vy-vx)<-100){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "right!");
  //       send_x = 0;
        send_y = -1;
        send_z = vz;   
      }
      
      if (send_x == 0 && send_y == 0){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "stay!");
      }
    }
    else{
      // left hand side
//       APP_LOG(APP_LOG_LEVEL_DEBUG,"LEFT HAND PLAYER :->");
      // back
      if ( (vx > 300 && (vx-vy)>200) || (vx>600)){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "back!");
        send_x = -1;
  //       send_y = 0;
        send_z = vz;
      }
      
      // front
      if ( vx < -100 && (vx-vy)<-100 ){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "front!");
        send_x = 1;
  //       send_y = 0;
        send_z = vz;
      }
      
      // right
      if (vy>150 && (vy-vx)>100){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "right!");
  //       send_x = 0;
        send_y = -1;
        send_z = vz;
      }
        
      // left
      if (vy<-300 && (vy-vx)<-100){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "left!");
  //       send_x = 0;
        send_y = 1;
        send_z = vz;   
      }
      
      if (send_x == 0 && send_y == 0){
        APP_LOG(APP_LOG_LEVEL_DEBUG, "stay!");
      }
      
    }
    
    
    // send to android
    DictionaryIterator *iter;
    AppMessageResult reason = app_message_outbox_begin(&iter);
    while (reason == APP_MSG_BUSY) {
      reason = app_message_outbox_begin(&iter);
      APP_LOG(APP_LOG_LEVEL_DEBUG, "sending message busy");
    }
      
    dict_write_int(iter, KEY_X, &send_x,4,true);
    dict_write_int(iter, KEY_Y, &send_y,4,true);
    dict_write_int(iter, KEY_Z, &send_z,4,true);
    app_message_outbox_send();
   
    
    APP_LOG(APP_LOG_LEVEL_DEBUG, "sent!");
    APP_LOG(APP_LOG_LEVEL_DEBUG, " sent! %d %d", send_x, send_y);
    
//     // sleep for a while
//     for (int j=0;j<100;j++);
    count = 1;
    
    // clear send value
    send_x = 0;
    send_y = 0;
    send_z = 0;   
  }
    
  }

}

// register callback
// static void inbox_received_callback(DictionaryIterator *iterator, void *context) {
//  APP_LOG(APP_LOG_LEVEL_ERROR, "Inbox Message dropped!");
// }

// receive left/right hand
static void inbox_received_callback(DictionaryIterator *iterator, void *context) {
 APP_LOG(APP_LOG_LEVEL_DEBUG, "Inbox Message received!");
 Tuple *t = dict_read_first(iterator);
   // For all items
   while(t != NULL) {
       // Which key was received?
       switch(t->key) {
           case WHICH_HAND_KEY:
               left_hand= t->value->int32;
               APP_LOG(APP_LOG_LEVEL_DEBUG, "left hand: %d",(int)t->key);
               if (left_hand == -1){
                 APP_LOG(APP_LOG_LEVEL_DEBUG, "Right Hand Player");
               }
               else{
                 APP_LOG(APP_LOG_LEVEL_DEBUG, "Left Hand Player");
               }

                   break;
           default:
               APP_LOG(APP_LOG_LEVEL_ERROR, "Key %d not recognized!", (int)t->key);
               break;
       }
       t = dict_read_next(iterator);
   }
}

static void inbox_dropped_callback(AppMessageResult reason, void *context) {
 APP_LOG(APP_LOG_LEVEL_ERROR, "Inbox Message dropped!");
}

static void outbox_failed_callback(DictionaryIterator *iterator, AppMessageResult reason, void *context) {
   APP_LOG(APP_LOG_LEVEL_ERROR, "Outbox send failed!");
}

static void outbox_sent_callback(DictionaryIterator *iterator, void *context) {
   APP_LOG(APP_LOG_LEVEL_INFO, "Outbox send success!");
}

static void tap_handler(AccelAxisType axis, int32_t direction) {
  switch (axis) {
  case ACCEL_AXIS_X:
    if (direction > 0) {
      text_layer_set_text(s_output_layer, "X axis positive.");
    } else {
      text_layer_set_text(s_output_layer, "X axis negative.");
    }
    break;
  case ACCEL_AXIS_Y:
    if (direction > 0) {
      text_layer_set_text(s_output_layer, "Y axis positive.");
    } else {
      text_layer_set_text(s_output_layer, "Y axis negative.");
    }
    break;
  case ACCEL_AXIS_Z:
    if (direction > 0) {
      text_layer_set_text(s_output_layer, "Z axis positive.");
    } else {
      text_layer_set_text(s_output_layer, "Z axis negative.");
    }
    break;
  }
}

static void main_window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect window_bounds = layer_get_bounds(window_layer);

  //Create background bitmap and bitmap layer
	s_bitmap_drum = gbitmap_create_with_resource(RESOURCE_ID_IMAGE_DRUM);
	s_bitmap_layer = bitmap_layer_create(GRect(0, 0, window_bounds.size.w, window_bounds.size.h));
	bitmap_layer_set_bitmap(s_bitmap_layer, s_bitmap_drum);
	layer_add_child(window_layer, bitmap_layer_get_layer(s_bitmap_layer));
  
   // Create output TextLayer
  s_output_layer = text_layer_create(GRect(0, window_bounds.size.h/2, window_bounds.size.w, 40));
  text_layer_set_background_color(s_output_layer, GColorBlack);
	text_layer_set_text_color(s_output_layer, GColorWhite);
	text_layer_set_text_alignment(s_output_layer, GTextAlignmentCenter);
  text_layer_set_font(s_output_layer, fonts_get_system_font(FONT_KEY_GOTHIC_24_BOLD));
  text_layer_set_text(s_output_layer, "PebbAirDrum");
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));
}

static void main_window_unload(Window *window) {
  // Destroy output TextLayer
  text_layer_destroy(s_output_layer);
  // Destroy Bitmap
  gbitmap_destroy(s_bitmap_drum);
	bitmap_layer_destroy(s_bitmap_layer);
}

static void init() {
  // Create main Window
  s_main_window = window_create();
  window_set_window_handlers(s_main_window, (WindowHandlers) {
    .load = main_window_load,
    .unload = main_window_unload
  });
  window_stack_push(s_main_window, true);

  // Use tap service? If not, use data service
  if (TAP_NOT_DATA) {
    // Subscribe to the accelerometer tap service
    accel_tap_service_subscribe(tap_handler);
  } else {
    // Subscribe to the accelerometer data service
    int num_samples = 5;
    nums = num_samples;
    accel_data_service_subscribe(num_samples, data_handler);

    // Choose update rate
    accel_service_set_sampling_rate(ACCEL_SAMPLING_10HZ);
    
     // Register callbacks
   app_message_register_inbox_received(inbox_received_callback);
   app_message_register_inbox_dropped(inbox_dropped_callback);
   app_message_register_outbox_failed(outbox_failed_callback);
   app_message_register_outbox_sent(outbox_sent_callback);
   //Open AppMessage
   app_message_open(app_message_inbox_size_maximum(), app_message_outbox_size_maximum());
  }
}


static void deinit() {
  // Destroy main Window
  window_destroy(s_main_window);

  if (TAP_NOT_DATA) {
    accel_tap_service_unsubscribe();
  } else {
    accel_data_service_unsubscribe();
  }
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}