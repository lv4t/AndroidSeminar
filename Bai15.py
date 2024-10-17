# Điều khiển nhân vật bằng phím mũi tên

import pygame, sys
from pygame.locals import *

pygame.init()

# Xác định FPS = Frame per second
FPS = 120 # 24
fpsClock = pygame.time.Clock()

width = 800
height = 600
window = pygame.display.set_mode((width, height))
pygame.display.set_caption('Cửa sổ game')

class Character:
    def __init__(self, x, y, link, size):
        self.x = x
        self.y = y
        self.size = size
        self.image = pygame.image.load(link)

    def draw(self, surface):
        self.image = pygame.transform.scale(self.image, self.size)
        surface.blit(self.image, (self.x, self.y))

    def run(self, moveLeft, moveRight):
        if moveLeft == True:
            self.x = self.x - 2
        if moveRight == True:
            self.x = self.x + 2

        if self.x + 100 > width:
            self.x = width - 100
        if self.x < 0:
            self.x = 0

char = Character(330, 400, 'char.png', (100, 100))
moveLeft = False
moveRight = False
running = True
while running == True:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

        if event.type == KEYDOWN:
            if event.key == K_LEFT:
                moveLeft = True
            if event.key == K_RIGHT:
                moveRight = True
        
        if event.type == KEYUP:
            if event.key == K_LEFT:
                moveLeft = False
            if event.key == K_RIGHT:
                moveRight = False

    window.fill((255, 255, 255))

    char.draw(window)
    char.run(moveLeft, moveRight)

    pygame.display.update()
    fpsClock.tick(FPS)

pygame.quit()