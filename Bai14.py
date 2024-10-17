# Tạo cửa sổ và tiêu đề cho game
# Bổ sung câu lệnh thay đổi kích thước nhân vật
# Viết lại lớp Character để tái sử dụng thêm nhiều nhân vật cùng 1 lúc

import pygame

pygame.init() # Gọi thư viện pygame

width = 800
height = 600
window = pygame.display.set_mode((width, height)) # Đặt kích thước cho cửa sổ game
pygame.display.set_caption('Cửa sổ game') # Đặt tiêu đề cho cửa sổ game

class Character:
    def __init__(self, x, y, link, size):
        self.x = x
        self.y = y
        self.size = size
        self.link = link
        self.image = pygame.image.load(link) # Tải hình ảnh nhân vật từ thư mục vào chương trình

    def draw(self, surface):
        self.image = pygame.transform.scale(self.image, self.size) # Điều chỉnh lại kích thước nhân vật
        surface.blit(self.image, (self.x, self.y)) # Đặt nhân vật vào cửa sổ game được chỉ định

char = Character(330, 400, 'char.png', (100, 100)) # Tạo một nhân vật với các thuộc tính (vị trí x, vị trị y, đường dẫn đến hình ảnh nhân vật, kích thước nhân vật)

running = True
while running == True:
    # event: sự kiện - Là những thao tác của người dùng lên bàn phím hoặc chuột 
    for event in pygame.event.get(): # Cho vòng lặp quét qua các sự kiện
        if event.type == pygame.QUIT: # Kiểm tra xem loại sự kiện được nhấn có phải là nút thoát game không
            running = False # Đặt biến running bằng False để thoát vòng lặp

    window.fill((255, 255, 255)) # Tạo màu nền cho cửa sổ game

    char.draw(window) # Đặt nhân vật vào cửa sổ game

    pygame.display.update() # Liên tục cập nhật những thay đổi lên màn hình game

pygame.quit()