# Nhân bản đối tượng và tính điểm

import pygame, random
from pygame.locals import *

pygame.init()

# Xác định FPS = Frame per second
FPS = 120 # 24
fpsClock = pygame.time.Clock()

# Thiết lập thông số cho game
width = 800
height = 600
window = pygame.display.set_mode((width, height))
pygame.display.set_caption('Cửa sổ game')

bg = pygame.image.load('bg.png')
bg = pygame.transform.scale(bg, (width, height))

font = pygame.font.SysFont('consolas', 30)
textSurface = font.render(" Catch him! ", True, (255, 255, 255), (83, 186, 92)) 

# Nhạc nền
bg_music = pygame.mixer.Sound('bg_music.mp3')
bg_music.play(loops=-1)
bg_music.set_volume(0.5) # 0-1

# Các hiệu ứng âm thanh
pop_fx = pygame.mixer.Sound('pop.wav')
pop_fx.set_volume(1)

class Character:
    def __init__(self, x, y, link, size):
        self.x = x
        self.y = y
        self.size = size
        self.image = pygame.image.load(link)

    def draw(self, surface):
        self.image = pygame.transform.scale(self.image, self.size)
        surface.blit(self.image, (self.x, self.y))

    def run(self, moveLeft, moveRight, moveUp, moveDown):
        if moveLeft == True:
            self.x = self.x - 2
        if moveRight == True:
            self.x = self.x + 2
        if moveUp == True:
            self.y = self.y - 2
        if moveDown == True:
            self.y = self.y + 2

        if self.x + 100 > width:
            self.x = width - 100
        if self.x < 0:
            self.x = 0
        if self.y + 100 > height:
            self.y = height - 100
        if self.y < 0:
            self.y = 0

# Các chương trình con
def collision(surface1, pos1, surface2, pos2):
    mask1 = pygame.mask.from_surface(surface1)
    mask2 = pygame.mask.from_surface(surface2)
    x = pos2[0] - pos1[0]
    y = pos2[1] - pos1[1]
    if mask1.overlap(mask2, (x, y)) != None:
        return True
    return False

# Khởi tạo nhân vật
char = Character(330, 400, 'char.png', (100, 100))
char2 = Character(150, 400, 'char2.png', (100, 100))

# Biến chương trình
moveLeft = False
moveRight = False
moveUp = False
moveDown = False

# Biến hệ thống
running = True
sound_check = True
score = 0

# Chương trình chính
while running == True:

    # Các câu lệnh xử lí sự kiện
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

        if event.type == KEYDOWN:
            if event.key == K_LEFT:
                moveLeft = True
            if event.key == K_RIGHT:
                moveRight = True
            if event.key == K_UP:
                moveUp = True
            if event.key == K_DOWN:
                moveDown = True
        
        if event.type == KEYUP:
            if event.key == K_LEFT:
                moveLeft = False
            if event.key == K_RIGHT:
                moveRight = False
            if event.key == K_UP:
                moveUp = False
            if event.key == K_DOWN:
                moveDown = False
    
    # Cài đặt ảnh nền
    window.blit(bg, (0, 0))
    window.blit(textSurface, (300, 100))

    # Các câu lệnh cho nhân vât char
    char.draw(window)
    char.run(moveLeft, moveRight, moveUp, moveDown)

    # Các câu lệnh tương tác giữa 2 nhân vật
    if collision(char.image, (char.x, char.y), char2.image, (char2.x, char2.y)) == False:
        char2.draw(window)
        sound_check = True
    else:
        if sound_check == True:
            pop_fx.play()
            score = score + 1
            char2.x = random.randrange(100, width-100)
            char2.y = random.randrange(100, height-100)
            print(score)
            sound_check = False

    # Các câu lệnh hệ thống
    pygame.display.update()
    fpsClock.tick(FPS)

pygame.quit()